package com.backmask.timelapse.infuse.message;

import android.util.JsonReader;

import java.io.IOException;

public abstract class BaseMessage {

    public enum Status {
        OK,
        ERROR,
        INVALID
    }

    public Status status;

    public boolean readMessage(JsonReader reader) throws IOException {
        reader.beginObject();
        status = readStatus(reader);
        boolean readBody = tryReadBody(reader);
        reader.endObject();

        return status != Status.INVALID && readBody;
    }

    private Status readStatus(JsonReader reader) throws IOException {
        String statusName = reader.nextName();
        if (!statusName.equals("status")) {
            return Status.INVALID;
        }

        String statusValue = reader.nextString();
        if (statusValue.equals("ok")) {
            return Status.OK;
        } else if (statusName.equals("error")) {
            return Status.ERROR;
        }

        return Status.INVALID;
    }

    protected abstract boolean tryReadBody(JsonReader reader) throws IOException;
}
