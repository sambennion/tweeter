package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.observer.IGetFollowingCountObserver;

public class GetFollowingCountHandler extends BackgroundTaskHandler<IGetFollowingCountObserver> {

    public GetFollowingCountHandler(IGetFollowingCountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(IGetFollowingCountObserver observer, Bundle data) {
        int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.handleFollowingCountSuccess(count);
    }
}
