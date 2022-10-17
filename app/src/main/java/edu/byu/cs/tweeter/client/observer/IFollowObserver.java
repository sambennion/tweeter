package edu.byu.cs.tweeter.client.observer;

public interface IFollowObserver extends FollowChangeObserver{
    void handleSuccess();
}
