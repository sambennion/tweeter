package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStatusesHandler extends GetPagedHandler<GetStatusesObserver>{
    public GetStatusesHandler(GetStatusesObserver observer) {
        super(observer);
    }
}
