package edu.byu.cs.tweeter.client.observer;

public interface UnfollowObserver extends FollowChangeObserver{
    void handleUnfollowSuccess();
}
