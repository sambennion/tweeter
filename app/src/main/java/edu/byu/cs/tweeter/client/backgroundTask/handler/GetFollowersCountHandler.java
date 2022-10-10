package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.observer.GetFollowersCountObserver;

public class GetFollowersCountHandler extends BackgroundTaskHandler<GetFollowersCountObserver>{
    public GetFollowersCountHandler(GetFollowersCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(GetFollowersCountObserver observer, Bundle data) {
        int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
        observer.handleFollowerCountSuccess(count);
    }
}
