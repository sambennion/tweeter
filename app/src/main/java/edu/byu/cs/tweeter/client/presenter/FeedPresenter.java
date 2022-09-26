package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements StatusService.GetStatusesObserver, UserService.GetUserObserver{
    private static final String LOG_TAG = "FeedPresenter";
    private static final int PAGE_SIZE = 10;
    private final FeedPresenter.View view;
    private final User user;
    private final AuthToken authToken;

    private Status lastStatus;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public FeedPresenter(FeedPresenter.View view, User user, AuthToken authToken){
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }
    /**
     * The interface by which this presenter communicates with it's view.
     */
    public interface View {
        void setLoading(boolean value);
        void setHasMorePages(boolean hasMorePages);
        void addItems(List<Status> newStatuses);
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
        void navigateToUser(User user);
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
    }
    private void setLastStatus(Status status) {
        this.lastStatus = status;
    }

    private void setHasMorePages(boolean hasMorePages){
        this.hasMorePages = hasMorePages;
    }

    @Override
    public void handleSuccess(List<Status> statuses, boolean hasMorePages) {
        setLastStatus((statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null);
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
    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);
            getFeed(authToken, user, PAGE_SIZE, lastStatus);
        }
    }
    public void initiateGetUser(String alias){

        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(alias, this);
    }
    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        getStatusService().getFeed(authToken, targetUser, limit, lastStatus, this);
    }
    private StatusService getStatusService(){
        return new StatusService();
    }


}
