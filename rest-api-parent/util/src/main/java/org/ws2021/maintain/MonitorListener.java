package org.ws2021.maintain;

public interface MonitorListener {
    public void onError(String text, Throwable t);
    public void onPanic(String text, Throwable t);
    public void onTaskStart(String text);
    public void onTaskEnd(String text);
}
