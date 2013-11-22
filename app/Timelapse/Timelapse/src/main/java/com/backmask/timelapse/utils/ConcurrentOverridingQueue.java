package com.backmask.timelapse.utils;

import java.util.AbstractQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConcurrentOverridingQueue<T> extends AbstractQueue<T> {

    private Map<Integer, T> m_container = Collections.synchronizedMap(new LinkedHashMap<Integer, T>(10, (float) .75, false));

    @Override
    public boolean offer(T t) {
        m_container.put(t.hashCode(), t);
        return true;
    }

    @Override
    public T poll() {
        synchronized (m_container) {
            Iterator<T> it = m_container.values().iterator();
            if (it.hasNext()) {
                T val = it.next();
                m_container.remove(val.hashCode());
                return val;
            }
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return m_container.values().iterator();
    }

    @Override
    public int size() {
        return m_container.size();
    }

    @Override
    public T peek() {
        synchronized (m_container) {
            Iterator<T> it = m_container.values().iterator();
            if (it.hasNext()) {
                T val = it.next();
                return val;
            }
        }
        return null;
    }
}
