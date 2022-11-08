package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedStatusPresenter implements GetStatusesObserver {
    private static final String LOG_TAG = "StoryPresenter";

    public StoryPresenter(PagedStatusView view, User user, AuthToken authToken){
        super(view, user, authToken);
    }

    public StoryPresenter(User user, AuthToken authToken){
        super(user, authToken);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        getStatusService().getStory(authToken, targetUser, pageSize, lastItem, this);
    }

    @Override
    protected String getDescription() {
        return "Story: ";
    }

}
