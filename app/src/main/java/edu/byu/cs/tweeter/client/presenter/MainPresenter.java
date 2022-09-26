package edu.byu.cs.tweeter.client.presenter;

import java.util.List;
import java.util.concurrent.ExecutorService;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements FollowService.IsFollowerObserver, FollowService.UnfollowObserver,
        FollowService.FollowObserver, StatusService.PostStatusObserver, UserService.LogoutObserver,
        FollowService.GetFollowersCountObserver, FollowService.GetFollowingCountObserver {
    private final MainPresenter.View view;

    public MainPresenter(View view) {
        this.view = view;
    }

    @Override
    public void handleSuccess(boolean isFollower) {
        if (isFollower) {
            view.displayFollowingButton();
        } else {
            view.displayFollowButton();
        }
    }
    @Override
    public void handleSuccess() {
        view.displayInfoMessage("Successfully Posted!");
    }


    @Override
    public void handleUnfollowSuccess() {

        view.updateFollowingsFollowers(true);
    }

    @Override
    public void handleUnfollowFailure(String message) {
        view.displayErrorMessage("Failed to unfollow: " + message);
    }

    @Override
    public void handleUnfollowException(Exception ex) {
        view.displayErrorMessage("Failed to unfollow because of an exception: " + ex.getMessage());
    }

    @Override
    public void handleFollowSuccess() {
        view.updateFollowingsFollowers(false);
    }

    @Override
    public void handleFollowFailure(String message) {
        view.displayErrorMessage("Failed to follow: " + message);

    }

    @Override
    public void handleFollowException(Exception ex) {
        view.displayErrorMessage("Failed to follow because of an exception: " + ex.getMessage());
    }

    @Override
    public void handleEnableFollowButton() {
        view.enableFollowButton();
    }


//    @Override
//    public void handleSuccess(List<User> followees, boolean hasMorePages) {
//
//    }

    @Override
    public void handleFollowerCountSuccess(int count) {
        view.displayFollowerCount(count);
    }
    @Override
    public void handleFollowingCountSuccess(int count) {
        view.displayFollowingCount(count);
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage(message);
    }

    @Override
    public void handleException(Exception ex) {
        view.displayErrorMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
    }

    public void initiateLogout(AuthToken currUserAuthToken) {
        new UserService().logout(currUserAuthToken, this);
    }
    public void initiateGetFollowersAndFollowingCountTask(AuthToken currUserAuthToken, User user) {
        new FollowService().getFollowersAndFollowingCount(currUserAuthToken, user, this, this);
    }


    @Override
    public void logoutSucceeded() {
        view.logoutUser();
    }

    @Override
    public void logoutFailed(String message) {
        view.displayErrorMessage(message);
    }

    public interface View {
        void displayFollowerCount(int count);
        void displayFollowingCount(int count);
        void displayErrorMessage(String message);
        void displayInfoMessage(String message);
        void displayFollowingButton();
        void displayFollowButton();
        void logoutUser();

        void enableFollowButton();

        void updateFollowingsFollowers(boolean isRemoved);
//        void navigateToUser(User user);
    }

    public void initiateIsFollowerTask(AuthToken authToken, User user, User selected){
        new FollowService().isFollowers(authToken, user, selected, this);
    }
    public void initiateUnfollow(AuthToken authToken, User user){
        new FollowService().unfollow(authToken, user, this);
    }
    public void initiateFollow(AuthToken authToken, User user){
        new FollowService().follow(authToken, user, this);
    }
    public void initiatePostStatus(AuthToken authToken, Status status){
        new StatusService().postStatus(authToken, status, this);
    }
}
