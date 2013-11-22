package com.backmask.timelapse.arduino;

import com.backmask.timelapse.utils.ConcurrentOverridingQueue;

import org.shokai.firmata.ArduinoFirmata;

public class ArduinoCommanderWorker implements Runnable {

    private ArduinoCommanderListener m_listener;
    private ArduinoFirmata m_firmata;
    private ConcurrentOverridingQueue<ArduinoCommanderMessage> m_queue;

    public ArduinoCommanderWorker(ArduinoFirmata firmata, ArduinoCommanderListener listener) {
        m_listener = listener;
        m_firmata = firmata;
        m_queue = new ConcurrentOverridingQueue<ArduinoCommanderMessage>();
    }

    public void queueMessage(ArduinoCommanderMessage message) {
        if (!m_firmata.isOpen()) return;
        m_queue.add(message);
    }

    @Override
    public void run() {
        try {
            m_firmata.connect();
            m_listener.onConnectivityUpdate(m_firmata.isOpen());

            while (m_firmata.isOpen()) {
                if (!m_queue.isEmpty()) {
                    m_queue.poll().visit(m_firmata);
                }
            }
        } catch (Exception e) {
            m_listener.onException(e);
        }

        m_listener.onConnectivityUpdate(false);
    }
}
