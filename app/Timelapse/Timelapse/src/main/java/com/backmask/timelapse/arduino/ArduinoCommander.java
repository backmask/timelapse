package com.backmask.timelapse.arduino;


import android.app.Activity;

import org.shokai.firmata.ArduinoFirmata;


public class ArduinoCommander {

    private ArduinoCommanderWorker m_worker;
    private ArduinoCommanderListener m_listener;
    private Thread m_workerThread;

    public static int DISCONNECT = 1;
    public static int SERVO_LEFT_WHEEL = 13;
    public static int SERVO_RIGHT_WHEEL = 12;
    public static int SERVO_ARM_ROTATION = 9;
    public static int SERVO_ARM_HEIGHT = 10;

    public ArduinoCommander(Activity activity, ArduinoCommanderListener listener) {
        m_worker = new ArduinoCommanderWorker(new ArduinoFirmata(activity), listener);
        m_listener = listener;
    }

    public void setServoRotation(final int servoPid, final int value) {
        m_worker.queueMessage(new ArduinoCommanderMessage(servoPid) {
            @Override
            public void visit(ArduinoFirmata firmata) {
                firmata.servoWrite(servoPid, value);
            }
        });
    }

    public void connect() {
        if (m_workerThread != null && m_workerThread.isAlive()) {
            m_listener.onException(new Exception("Connection is not closed, cannot reconnect"));
            return;
        }

        m_workerThread = new Thread(m_worker);
        m_workerThread.start();
    }

    public void disconnect() {
        m_worker.queueMessage(new ArduinoCommanderMessage(DISCONNECT) {
            @Override
            public void visit(ArduinoFirmata firmata) {
                firmata.reset();
                firmata.close();
            }
        });
    }
}
