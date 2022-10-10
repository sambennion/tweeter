package edu.byu.cs.tweeter.client.observer;

import edu.byu.cs.tweeter.model.domain.User;

public interface GetUserObserver extends ServiceObserver{
    void getUserSucceeded(User user);

    void getUserFailed(String message);
}
