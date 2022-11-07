package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import edu.byu.cs.tweeter.client.observer.GetStatusesObserver;

public class GetStatusesHandler extends GetPagedHandler<GetStatusesObserver>{
    public GetStatusesHandler(GetStatusesObserver observer) {
        super(observer);
    }
}
