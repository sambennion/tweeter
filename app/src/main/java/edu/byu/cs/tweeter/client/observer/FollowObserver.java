package edu.byu.cs.tweeter.client.observer;

public interface FollowObserver extends FollowChangeObserver{
    void handleFollowSuccess();
}
