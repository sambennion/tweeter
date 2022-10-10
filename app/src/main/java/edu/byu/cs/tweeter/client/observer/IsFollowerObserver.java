package edu.byu.cs.tweeter.client.observer;

public interface IsFollowerObserver extends ServiceObserver {
    void handleSuccess(boolean isFollower);
}
