package com.backmask.timelapse.infuse;

public interface ISocketWriter {
    void write(String queueName, byte[] bytes);
}
