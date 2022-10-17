package edu.byu.cs.tweeter.client.backgroundTask.handler;

import edu.byu.cs.tweeter.client.observer.IGetFollowersObserver;

public class GetFollowersHandler extends GetPagedHandler<IGetFollowersObserver>{
    public GetFollowersHandler(IGetFollowersObserver observer) {
        super(observer);
    }
}
