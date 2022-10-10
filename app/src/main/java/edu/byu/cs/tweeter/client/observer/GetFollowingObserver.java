package edu.byu.cs.tweeter.client.observer;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface GetFollowingObserver extends ServiceObserver{
    void handleSuccess(List<User> followees, boolean hasMorePages);
}
