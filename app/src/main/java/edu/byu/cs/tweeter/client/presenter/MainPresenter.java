package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.observer.FollowObserver;
import edu.byu.cs.tweeter.client.observer.GetFollowersCountObserver;
import edu.byu.cs.tweeter.client.observer.GetFollowingCountObserver;
import edu.byu.cs.tweeter.client.observer.IsFollowerObserver;
import edu.byu.cs.tweeter.client.observer.PostStatusObserver;
import edu.byu.cs.tweeter.client.observer.UnfollowObserver;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter implements IsFollowerObserver, UnfollowObserver,
        FollowObserver, PostStatusObserver, UserService.LogoutObserver,
        GetFollowersCountObserver, GetFollowingCountObserver {
    private final MainPresenter.View view;

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
    }

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

//    @Override
//    public void handleUnfollowFailure(String message) {
//        view.displayErrorMessage("Failed to unfollow: " + message);
//    }
//
//    @Override
//    public void handleUnfollowException(Exception ex) {
//        view.displayErrorMessage("Failed to unfollow because of an exception: " + ex.getMessage());
//    }

    @Override
    public void handleFollowSuccess() {
        view.updateFollowingsFollowers(false);
    }

//    @Override
//    public void handleFollowFailure(String message) {
//        view.displayErrorMessage("Failed to follow: " + message);
//
//    }
//
//    @Override
//    public void handleFollowException(Exception ex) {
//        view.displayErrorMessage("Failed to follow because of an exception: " + ex.getMessage());
//    }

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

    public void initiatePost(String post, User currUser) {
        Status newStatus;
        try {
            newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            initiatePostStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus);
        } catch (Exception ex){
            view.displayErrorMessage("Exception while taking status input");
        }
    }
    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }
    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }
    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
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
