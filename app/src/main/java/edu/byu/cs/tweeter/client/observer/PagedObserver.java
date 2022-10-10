package edu.byu.cs.tweeter.client.observer;

import java.util.List;

public interface PagedObserver<T> extends ServiceObserver{
    void handleSuccess(List<T> items, boolean hasMorePages);
}
