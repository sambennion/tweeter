package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.observer.LogoutObserver;

public class LogoutHandler extends BackgroundTaskHandler<LogoutObserver>{
    public LogoutHandler(LogoutObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(LogoutObserver observer, Bundle data) {
        observer.logoutSucceeded();
    }
}
