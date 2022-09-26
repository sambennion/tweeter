package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The presenter for the "following" functionality of the application.
 */
public class FollowingPresenter implements FollowService.GetFollowingObserver, UserService.GetUserObserver {

    private static final String LOG_TAG = "FollowingPresenter";
    public static final int PAGE_SIZE = 10;

    private final View view;
    private final User user;
    private final AuthToken authToken;

    private User lastFollowee;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
    }

    @Override
    public void getUserFailed(String message) {
        view.displayErrorMessage(message);
    }

    /**
     * The interface by which this presenter communicates with it's view.
     */
    public interface View {
        void setLoading(boolean value);
        void setHasMorePages(boolean hasMorePages);
        void addItems(List<User> newUsers);
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
        void navigateToUser(User user);
        void removeDummyFollower();
    }

    /**
     * Creates an instance.
     *
     * @param view      the view for which this class is the presenter.
     * @param user      the user that is currently logged in.
     * @param authToken the auth token for the current session.
     */
    public FollowingPresenter(View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }
    public void initiateGetUser(String userAlias){
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(userAlias, this);
//        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
//                userAlias, new FollowService.GetUserHandler(this));
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(getUserTask);
    }

    public User getLastFollowee() {
        return lastFollowee;
    }

    private void setLastFollowee(User lastFollowee) {
        this.lastFollowee = lastFollowee;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    private void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
        view.setHasMorePages(hasMorePages);
    }

    public boolean isLoading() {
        return isLoading;
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
    }

    /**
     * Called by the view to request that another page of "following" users be loaded.
     */
    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);
            view.removeDummyFollower();
            getFollowing(authToken, user, PAGE_SIZE, lastFollowee);
        }
    }

    /**
     * Requests the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned for a previous request. This is an asynchronous
     * operation.
     *
     * @param authToken    the session auth token.
     * @param targetUser   the user for whom followees are being retrieved.
     * @param limit        the maximum number of followees to return.
     * @param lastFollowee the last followee returned in the previous request (can be null).
     */
    public void getFollowing(AuthToken authToken, User targetUser, int limit, User lastFollowee) {
        getFollowingService().getFollowees(authToken, targetUser, limit, lastFollowee, this);
    }

    /**
     * Returns an instance of {@link FollowService}. Allows mocking of the FollowService class
     * for testing purposes. All usages of FollowService should get their FollowService
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    public FollowService getFollowingService() {
        return new FollowService();
    }

    /**
     * Adds new followees retrieved asynchronously from the service to the view.
     *
     * @param followees    the retrieved followees.
     * @param hasMorePages whether or not there are more followees to be retrieved.
     */
    @Override
    public void handleSuccess(List<User> followees, boolean hasMorePages) {
        setLastFollowee((followees.size() > 0) ? followees.get(followees.size() - 1) : null);
        setHasMorePages(hasMorePages);
//        view.removeDummyFollower();

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