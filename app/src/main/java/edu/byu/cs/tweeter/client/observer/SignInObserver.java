package edu.byu.cs.tweeter.client.observer;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface SignInObserver extends ServiceObserver{
    void signInSucceeded(User user, AuthToken authToken);

}
