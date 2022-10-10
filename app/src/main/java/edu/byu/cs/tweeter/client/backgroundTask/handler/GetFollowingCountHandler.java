package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.observer.GetFollowingCountObserver;

public class GetFollowingCountHandler extends BackgroundTaskHandler<GetFollowingCountObserver> {

    public GetFollowingCountHandler(GetFollowingCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(GetFollowingCountObserver observer, Bundle data) {
        int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.handleFollowingCountSuccess(count);
    }
}
