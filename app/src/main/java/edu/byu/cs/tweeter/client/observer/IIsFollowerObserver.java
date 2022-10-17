package edu.byu.cs.tweeter.client.observer;

public interface IIsFollowerObserver extends ServiceObserver {
    void handleSuccess(boolean isFollower);
}
