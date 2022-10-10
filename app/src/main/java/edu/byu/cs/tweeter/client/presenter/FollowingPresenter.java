package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.observer.GetFollowingObserver;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The presenter for the "following" functionality of the application.
 */
public class FollowingPresenter extends PagedUserPresenter implements GetFollowingObserver, UserService.GetUserObserver {

    private static final String LOG_TAG = "FollowingPresenter";
//    public static final int PAGE_SIZE = 10;

    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
    }

    @Override
    public void getUserFailed(String message) {
        view.displayErrorMessage(message);
    }

    /**
     * Creates an instance.
     *
     * @param view      the view for which this class is the presenter.
     * @param user      the user that is currently logged in.
     * @param authToken the auth token for the current session.
     */
    public FollowingPresenter(PagedUserPresenter.PagedUserView view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }
    public void initiateGetUser(String userAlias){
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(userAlias, this);
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }


    @Override
    protected void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        getFollowService().getFollowees(authToken, targetUser, pageSize, lastItem, this);
    }

    @Override
    protected String getDescription() {
        return "Following: ";
    }
    /**
     * Adds new followees retrieved asynchronously from the service to the view.
     *
     * @param followees    the retrieved followees.
     * @param hasMorePages whether or not there are more followees to be retrieved.
     */
    @Override
    public void handleSuccess(List<User> followees, boolean hasMorePages) {
        setLastItem((followees.size() > 0) ? followees.get(followees.size() - 1) : null);
        setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(followees);
        setLoading(false);

    }

    /**
     * Notifies the presenter when asynchronous retrieval of followees failed.
     *
     * @param message error message.
     */
    @Override
    public void handleFailure(String message) {
        String errorMessage = "Failed to retrieve followees: " + message;
        Log.e(LOG_TAG, errorMessage);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }

    /**
     * Notifies the presenter that an exception occurred in an asynchronous method this class is
     * observing.
     *
     * @param exception the exception.
     */
    @Override
    public void handleException(Exception exception) {
        String errorMessage = "Failed to retrieve followees because of exception: " + exception.getMessage();
        Log.e(LOG_TAG, errorMessage, exception);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }
}