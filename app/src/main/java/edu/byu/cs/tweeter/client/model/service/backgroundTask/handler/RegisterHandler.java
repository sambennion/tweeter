package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.observer.RegisterObserver;

public class RegisterHandler extends SignInHandler<RegisterObserver> {
    public RegisterHandler(RegisterObserver observer) {
        super(observer);
    }
}
