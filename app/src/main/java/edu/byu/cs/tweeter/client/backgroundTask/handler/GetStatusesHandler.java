package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStatusesHandler extends BackgroundTaskHandler<GetStatusesObserver>{
    public GetStatusesHandler(GetStatusesObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(GetStatusesObserver observer, Bundle data) {
        List<Status> statuses = (List<Status>) data.getSerializable(GetStoryTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetStoryTask.MORE_PAGES_KEY);
        observer.handleSuccess(statuses, hasMorePages);
    }
}
