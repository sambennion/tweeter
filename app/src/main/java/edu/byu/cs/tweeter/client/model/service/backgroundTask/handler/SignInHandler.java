package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.observer.SignInObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class SignInHandler<T extends SignInObserver> extends BackgroundTaskHandler<T> {
    public SignInHandler(T observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(T observer, Bundle data) {
        User signedInUser = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);

        // Cache user session information
        Cache.getInstance().setCurrUser(signedInUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        observer.signInSucceeded(signedInUser, authToken);
    }
}
