package edu.byu.cs.tweeter.client.observer;

public interface GetFollowersCountObserver extends ServiceObserver{
    void handleFollowerCountSuccess(int count);
}
