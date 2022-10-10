package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedStatusPresenter extends PagedPresenter<Status>{
    public PagedStatusPresenter(PagedStatusView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    public interface PagedStatusView extends PagedView<Status>{

    }
}
