package edu.byu.cs.tweeter.client.observer;

public interface GetFollowingCountObserver extends ServiceObserver{
    void handleFollowingCountSuccess(int count);
}
