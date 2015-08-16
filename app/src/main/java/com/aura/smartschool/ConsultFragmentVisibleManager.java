package com.aura.smartschool;

/**
 * Created by Administrator on 2015-08-16.
 */
public class ConsultFragmentVisibleManager {
    private volatile static ConsultFragmentVisibleManager INSTANCE;

    private boolean consultFragmentVisible;
    private int visibleCategory;

    private ConsultFragmentVisibleManager() {}

    private OnMessageReceiveObserver msgObserver;

    public interface OnMessageReceiveObserver {
        void onReceived(String message);
    }

    public static ConsultFragmentVisibleManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ConsultFragmentVisibleManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConsultFragmentVisibleManager();
                }
            }
        }
        return INSTANCE;
    }

    public void setVisible(boolean isVisible, int category, OnMessageReceiveObserver observer) {
        this.consultFragmentVisible = isVisible;
        this.visibleCategory = category;
        this.msgObserver = observer;
    }

    public boolean isVisible(int category) {
        return consultFragmentVisible && (visibleCategory == category);
    }

    public void notifyMessageReceived(String message) {
        if (msgObserver != null) {
            msgObserver.onReceived(message);
        }
    }
}
