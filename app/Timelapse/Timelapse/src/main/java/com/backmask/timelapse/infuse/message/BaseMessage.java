package com.backmask.timelapse.infuse.message;

import android.util.JsonReader;

import java.io.IOException;

public abstract class BaseMessage {

    public enum Status {
        OK,
        ERROR,
        INVALID
    }

    public String method;
    public Status status;
    public int errorCode;
    public String errorMessage;

    public boolean readMessage(JsonReader reader) throws IOException {
        reader.beginObject();
        boolean successfullyRead = true;

        String statusName = reader.nextName();
        if ("method".equals(statusName)) {
            method = reader.nextString();
            statusName = reader.nextName();
        }

        if (!"status".equals(statusName)) {
            status = Status.INVALID;
            return false;
        }

        String statusValue = reader.nextString();
        if ("ok".equals(statusValue)) {
            status = Status.OK;
            if (reader.hasNext() && "data".equals(reader.nextName())) {
                reader.beginObject();
                successfullyRead = tryReadData(reader);
                reader.endObject();
            }
        } else if (statusName.equals("error")) {
            status = Status.ERROR;
            if (reader.hasNext() && "error".equals(reader.nextName())) {
                reader.beginObject();
                successfullyRead = tryReadError(reader);
                reader.endObject();
            }
        } else {
            status = Status.INVALID;
            return false;
        }

        reader.endObject();
        return status != Status.INVALID && successfullyRead;
    }

    protected abstract boolean tryReadData(JsonReader reader) throws IOException;

    protected boolean tryReadError(JsonReader reader) throws IOException
    {
        if (reader.hasNext() && "code".equals(reader.nextName())) errorCode = reader.nextInt();
        else return false;

        if (reader.hasNext() && "message".equals(reader.nextName())) errorMessage = reader.nextString();
        else return false;

        return true;
    }
}
