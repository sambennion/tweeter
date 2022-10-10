package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.observer.GetFollowersObserver;
import edu.byu.cs.tweeter.client.observer.GetUserObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedUserPresenter implements GetUserObserver, GetFollowersObserver {
//    private static final String LOG_TAG = "FollowersPresenter";

    /**
     * Creates an instance.
     *
     * @param view      the view for which this class is the presenter.
     * @param user      the user that is currently logged in.
     * @param authToken the auth token for the current session.
     */
    public FollowersPresenter(PagedUserPresenter.PagedUserView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        getFollowService().getFollowers(authToken, targetUser, pageSize, lastItem, this);
    }

    @Override
    protected String getDescription() {
        return "Followers: ";
    }


}
