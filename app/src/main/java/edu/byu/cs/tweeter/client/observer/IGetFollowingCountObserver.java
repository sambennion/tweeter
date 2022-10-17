package edu.byu.cs.tweeter.client.observer;

public interface IGetFollowingCountObserver extends ServiceObserver{
    void handleFollowingCountSuccess(int count);
}
