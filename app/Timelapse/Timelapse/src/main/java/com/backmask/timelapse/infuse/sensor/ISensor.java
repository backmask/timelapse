package com.backmask.timelapse.infuse.sensor;

import com.backmask.timelapse.infuse.ISocketWriter;

public interface ISensor {
    void attach(ISocketWriter writer);
    void detach();
}
