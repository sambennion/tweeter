package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.client.observer.GetUserObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedStatusPresenter implements GetStatusesObserver, GetUserObserver {
//    private static final String LOG_TAG = "FeedPresenter";

    public FeedPresenter(PagedStatusView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        getStatusService().getFeed(authToken, targetUser, pageSize, lastItem, this);
    }

    @Override
    protected String getDescription() {
        return "Feed: ";
    }

}
