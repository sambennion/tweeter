package edu.byu.cs.tweeter.client.observer;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface RegisterObserver extends ServiceObserver{
    void registerSucceeded(User user, AuthToken authToken);
}
