package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.observer.FollowObserver;

public class FollowHandler extends BackgroundTaskHandler<FollowObserver>{
    public FollowHandler(FollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowObserver observer, Bundle data) {
        observer.handleFollowSuccess();
        observer.handleEnableFollowButton();
    }
}
