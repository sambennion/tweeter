package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.observer.GetFollowingObserver;


public class GetFolloweesHandler extends GetPagedHandler<GetFollowingObserver> {
    public GetFolloweesHandler(GetFollowingObserver observer) {
        super(observer);
    }
}
