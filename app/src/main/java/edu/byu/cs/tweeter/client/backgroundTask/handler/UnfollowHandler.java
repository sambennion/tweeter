package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.observer.UnfollowObserver;

public class UnfollowHandler extends BackgroundTaskHandler<UnfollowObserver> {
    public UnfollowHandler(UnfollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(UnfollowObserver observer, Bundle data) {
        observer.handleUnfollowSuccess();
        observer.handleEnableFollowButton();
    }
}
