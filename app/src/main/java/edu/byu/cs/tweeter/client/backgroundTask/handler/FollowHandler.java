package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.observer.IFollowObserver;

public class FollowHandler extends BackgroundTaskHandler<IFollowObserver>{
    public FollowHandler(IFollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(IFollowObserver observer, Bundle data) {
        observer.handleSuccess();
        observer.handleEnableFollowButton();
    }
}
