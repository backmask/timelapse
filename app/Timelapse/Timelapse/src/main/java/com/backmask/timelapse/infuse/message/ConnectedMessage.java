package com.backmask.timelapse.infuse.message;

import android.util.JsonReader;

import java.io.IOException;

public class ConnectedMessage extends BaseMessage {
    public boolean isConnected;

    @Override
    public boolean tryReadData(JsonReader reader) throws IOException {
        String name = reader.nextName();
        if (!"connected".equals(name)) {
            return false;
        }

        isConnected = reader.nextBoolean();
        return true;
    }
}
