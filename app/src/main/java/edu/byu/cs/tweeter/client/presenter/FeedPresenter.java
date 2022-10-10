package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedStatusPresenter implements GetStatusesObserver, UserService.GetUserObserver {
    private static final String LOG_TAG = "FeedPresenter";
//    private static final int PAGE_SIZE = 10;


    public FeedPresenter(PagedStatusPresenter.PagedStatusView view, User user, AuthToken authToken){
        super(view, user, authToken);
    }


    @Override
    public void handleSuccess(List<Status> statuses, boolean hasMorePages) {
        setLastItem((statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null);
        setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(statuses);
        setLoading(false);
    }

    @Override
    public void handleFailure(String message) {
        String errorMessage = "Failed to get feed: " + message;
        Log.e(LOG_TAG, errorMessage);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void handleException(Exception exception) {
        String errorMessage = "Failed to retrieve feed because of exception: " + exception.getMessage();
        Log.e(LOG_TAG, errorMessage, exception);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
    }

    @Override
    public void getUserFailed(String message) {
        view.displayErrorMessage(message);
    }

    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        getStatusService().getFeed(authToken, targetUser, pageSize, lastItem, this);
    }

    @Override
    protected String getDescription() {
        return "Feed: ";
    }

    public void initiateGetUser(String alias){
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(alias, this);
    }

}
