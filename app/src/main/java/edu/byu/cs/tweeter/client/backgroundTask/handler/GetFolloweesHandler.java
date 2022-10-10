package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.observer.GetFollowingObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFolloweesHandler extends BackgroundTaskHandler<GetFollowingObserver>{
    public GetFolloweesHandler(GetFollowingObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(GetFollowingObserver observer, Bundle data) {
        List<User> followees = (List<User>) data.getSerializable(GetFollowingTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
        observer.handleSuccess(followees, hasMorePages);
    }
}
