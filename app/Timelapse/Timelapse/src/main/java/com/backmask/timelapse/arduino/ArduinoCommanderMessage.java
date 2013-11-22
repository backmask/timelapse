package com.backmask.timelapse.arduino;

import org.shokai.firmata.ArduinoFirmata;

public abstract class ArduinoCommanderMessage {
    private int m_messageId;

    public ArduinoCommanderMessage(int messageId) {
        m_messageId = messageId;
    }

    public abstract void visit(ArduinoFirmata firmata);

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            return o.hashCode() == hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return m_messageId;
    }
}