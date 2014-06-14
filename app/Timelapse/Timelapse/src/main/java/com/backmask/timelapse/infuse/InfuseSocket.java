package com.backmask.timelapse.infuse;

import android.app.Activity;
import android.util.Log;

import java.net.Socket;

public class InfuseSocket implements ISocketWriter {
    private Thread m_socketThread;
    private InfuseSocketWorker m_worker;
    private InfuseSocketListener m_listener;

    public InfuseSocket(Activity activity, String hostname, int port, InfuseSocketListener listener) {
        m_worker = new InfuseSocketWorker(activity, hostname, port, listener);
        m_listener = listener;
    }

    public void connect() {
        if (m_socketThread != null && m_socketThread.isAlive()) {
            m_listener.onException(new Exception("Connection is not closed, cannot reconnect"));
            return;
        }

        m_socketThread = new Thread(m_worker);
        m_socketThread.start();
    }

    public void send(String message) {
        m_worker.send(message);
    }

    public void disconnect() {
        m_worker.disconnect();
    }

    @Override
    public synchronized void write(String queueName, byte[] bytes) {
        m_worker.send("{\"__binaryLength\":" + bytes.length + ",\"hint\":\"" + queueName + "\"}");
        m_worker.send(bytes);
    }
}
