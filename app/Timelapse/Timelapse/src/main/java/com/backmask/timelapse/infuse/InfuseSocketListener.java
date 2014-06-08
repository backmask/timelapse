package com.backmask.timelapse.infuse;

public interface InfuseSocketListener {
    public void onConnectivityUpdate(boolean connected);

    public void onException(Exception e);
}
