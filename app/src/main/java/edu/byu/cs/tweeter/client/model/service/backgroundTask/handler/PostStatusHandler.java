package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.observer.PostStatusObserver;

public class PostStatusHandler extends BackgroundTaskHandler<PostStatusObserver>{
    public PostStatusHandler(PostStatusObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PostStatusObserver observer, Bundle data) {
        observer.handleSuccess();
    }
}
