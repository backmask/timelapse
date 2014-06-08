package com.backmask.timelapse.arduino;

import android.app.Activity;

import com.backmask.timelapse.utils.ConcurrentOverridingQueue;

import org.shokai.firmata.ArduinoFirmata;

public class ArduinoCommanderWorker implements Runnable {

    private ArduinoCommanderListener m_listener;
    private Activity m_activity;
    private ConcurrentOverridingQueue<ArduinoCommanderMessage> m_queue;

    public ArduinoCommanderWorker(Activity activity, ArduinoCommanderListener listener) {
        m_listener = listener;
        m_activity = activity;
        m_queue = new ConcurrentOverridingQueue<ArduinoCommanderMessage>();
    }

    public void queueMessage(ArduinoCommanderMessage message) {
        m_queue.add(message);
    }

    @Override
    public void run() {
        try {
            ArduinoFirmata firmata = new ArduinoFirmata(m_activity);
            firmata.connect();
            m_listener.onConnectivityUpdate(firmata.isOpen());

            while (firmata.isOpen()) {
                if (!m_queue.isEmpty()) {
                    m_queue.poll().visit(firmata);
                }
            }
        } catch (Exception e) {
            m_listener.onException(e);
        }

        m_listener.onConnectivityUpdate(false);
    }
}
