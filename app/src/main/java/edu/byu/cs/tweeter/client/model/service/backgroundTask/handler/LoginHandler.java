package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.observer.LoginObserver;

public class LoginHandler extends SignInHandler<LoginObserver>{
    public LoginHandler(LoginObserver observer) {
        super(observer);
    }
}
