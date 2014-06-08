package com.backmask.timelapse.infuse;

import android.app.Activity;
import android.util.JsonReader;
import android.util.Log;

import com.backmask.timelapse.R;
import com.backmask.timelapse.infuse.message.ConnectedMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by HEATSINK on 04/06/2014.
 */
public class InfuseSocketWorker implements Runnable {
    private Activity m_activity;
    private String m_hostname;
    private int m_port;
    private InfuseSocketListener m_listener;
    private Socket m_socket;
    private boolean m_disconnecting;

    public InfuseSocketWorker(Activity activity, String hostname, int port, InfuseSocketListener listener) {
        m_activity = activity;
        m_hostname = hostname;
        m_port = port;
        m_listener = listener;
        m_disconnecting = false;
    }

    public boolean isConnected() {
        return m_socket != null && m_socket.isConnected();
    }

    public void send(String message) {
        send(message.getBytes());
    }

    public void send(byte[] bytes) {
        if (!isConnected()) return;
        try {
            m_socket.getOutputStream().write(bytes);
        } catch (Exception e) {
            m_listener.onException(e);
        }
    }

    public void disconnect() {
        try {
            m_disconnecting = true;
            m_socket.close();
        } catch (Exception e) {} // We don't care anymore about errors on this connection
    }

    @Override
    public void run() {
        try {
            m_socket = new Socket(m_hostname, m_port);

            if (m_socket.isConnected()) {
                send(getDeviceDefinition());
                InputStream is = m_socket.getInputStream();
                JsonReader reader = new JsonReader(new InputStreamReader(is, "UTF-8"));

                reader.beginArray();
                ConnectedMessage connectedMessage = new ConnectedMessage();
                if (connectedMessage.readMessage(reader) && connectedMessage.isConnected) {
                    m_listener.onConnectivityUpdate(true);
                }
                reader.endArray();
                m_socket.close();
            }

        } catch (Exception e) {
            if (!m_disconnecting) {
                m_listener.onException(e);
            }
        }

        m_listener.onConnectivityUpdate(false);
    }

    private String getDeviceDefinition() {
        InputStream is = m_activity.getResources().openRawResource(R.raw.infuse_device);
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            m_listener.onException(e);
        }
        return total.toString();
    }
}
