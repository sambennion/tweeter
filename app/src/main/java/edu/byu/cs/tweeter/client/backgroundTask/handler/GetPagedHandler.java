package edu.byu.cs.tweeter.client.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.observer.PagedObserver;

public class GetPagedHandler<T extends PagedObserver> extends BackgroundTaskHandler<PagedObserver<T>>{
    public GetPagedHandler(T observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedObserver<T> observer, Bundle data) {
        List<T> items = (List<T>) data.getSerializable(GetStoryTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetStoryTask.MORE_PAGES_KEY);
        observer.handleSuccess(items, hasMorePages);
    }
}
