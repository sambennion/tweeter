package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedUserPresenter extends PagedPresenter<User> {
    public PagedUserPresenter(PagedView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }
    public interface PagedUserView extends PagedView<User>{

    }
    protected FollowService getFollowService() {
        return new FollowService();
    }
}
