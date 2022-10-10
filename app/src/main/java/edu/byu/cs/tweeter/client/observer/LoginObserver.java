package edu.byu.cs.tweeter.client.observer;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface LoginObserver extends ServiceObserver{
    void loginSucceeded(User user, AuthToken authToken);
}
