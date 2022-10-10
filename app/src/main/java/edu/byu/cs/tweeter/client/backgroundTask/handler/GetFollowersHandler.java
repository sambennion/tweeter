package edu.byu.cs.tweeter.client.backgroundTask.handler;

import edu.byu.cs.tweeter.client.observer.GetFollowersObserver;

public class GetFollowersHandler extends GetPagedHandler<GetFollowersObserver>{
    public GetFollowersHandler(GetFollowersObserver observer) {
        super(observer);
    }
}
