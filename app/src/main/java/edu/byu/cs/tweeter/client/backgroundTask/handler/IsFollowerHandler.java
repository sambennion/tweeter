package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.observer.IsFollowerObserver;

public class IsFollowerHandler extends BackgroundTaskHandler<IsFollowerObserver> {

    public IsFollowerHandler(IsFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(IsFollowerObserver observer, Bundle data) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}
