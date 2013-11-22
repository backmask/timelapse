package com.backmask.timelapse.arduino;

public interface ArduinoCommanderListener {
    public void onConnectivityUpdate(boolean connected);

    public void onException(Exception e);
}