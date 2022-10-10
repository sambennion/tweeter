package edu.byu.cs.tweeter.client.backgroundTask.handler;

import edu.byu.cs.tweeter.client.observer.GetFollowingObserver;


public class GetFolloweesHandler extends GetPagedHandler<GetFollowingObserver> {
    public GetFolloweesHandler(GetFollowingObserver observer) {
        super(observer);
    }
}
