package edu.byu.cs.tweeter.client.observer;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public interface GetStatusesObserver extends ServiceObserver{
    void handleSuccess(List<Status> statues, boolean hasMorePages);
}
