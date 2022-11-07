package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.observer.IUnfollowObserver;

public class UnfollowHandler extends BackgroundTaskHandler<IUnfollowObserver> {
    public UnfollowHandler(IUnfollowObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(IUnfollowObserver observer, Bundle data) {
        observer.handleSuccess();
        observer.handleEnableFollowButton();
    }
}
