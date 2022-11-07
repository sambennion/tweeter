package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.observer.IGetFollowersCountObserver;

public class GetFollowersCountHandler extends BackgroundTaskHandler<IGetFollowersCountObserver>{
    public GetFollowersCountHandler(IGetFollowersCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(IGetFollowersCountObserver observer, Bundle data) {
        int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
        observer.handleFollowerCountSuccess(count);
    }
}
