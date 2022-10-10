package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.observer.GetFollowersObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersHandler extends BackgroundTaskHandler<GetFollowersObserver>{
    public GetFollowersHandler(GetFollowersObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(GetFollowersObserver observer, Bundle data) {
        List<User> followers = (List<User>) data.getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
        observer.handleSuccess(followers, hasMorePages);
    }
}
