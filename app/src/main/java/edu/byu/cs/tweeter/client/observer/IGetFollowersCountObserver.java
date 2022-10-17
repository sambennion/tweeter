package edu.byu.cs.tweeter.client.observer;

public interface IGetFollowersCountObserver extends ServiceObserver{
    void handleFollowerCountSuccess(int count);
}
