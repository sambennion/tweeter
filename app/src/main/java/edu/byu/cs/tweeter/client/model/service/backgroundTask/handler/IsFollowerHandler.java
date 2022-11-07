package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.observer.IIsFollowerObserver;

public class IsFollowerHandler extends BackgroundTaskHandler<IIsFollowerObserver> {

    public IsFollowerHandler(IIsFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(IIsFollowerObserver observer, Bundle data) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}
