package edu.byu.cs.tweeter.client.observer;

public interface PostStatusObserver extends ServiceObserver{
    void handleSuccess();
}
