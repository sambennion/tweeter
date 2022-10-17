package edu.byu.cs.tweeter.client.observer;

public interface IUnfollowObserver extends FollowChangeObserver{
    void handleSuccess();
}
