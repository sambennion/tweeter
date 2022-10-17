package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.observer.ILogoutObserver;

public class LogoutHandler extends BackgroundTaskHandler<ILogoutObserver>{
    public LogoutHandler(ILogoutObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(ILogoutObserver observer, Bundle data) {
        observer.handleSuccess();
    }
}
