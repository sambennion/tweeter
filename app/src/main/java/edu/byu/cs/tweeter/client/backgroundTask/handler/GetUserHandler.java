package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.observer.GetUserObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserHandler extends BackgroundTaskHandler<GetUserObserver> {
    public GetUserHandler(GetUserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(GetUserObserver observer, Bundle data) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);

        observer.getUserSucceeded(user);
    }
}
