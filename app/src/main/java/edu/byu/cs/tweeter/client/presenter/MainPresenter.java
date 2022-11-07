package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.observer.IFollowObserver;
import edu.byu.cs.tweeter.client.observer.IGetFollowersCountObserver;
import edu.byu.cs.tweeter.client.observer.IGetFollowingCountObserver;
import edu.byu.cs.tweeter.client.observer.IIsFollowerObserver;
import edu.byu.cs.tweeter.client.observer.ILogoutObserver;
import edu.byu.cs.tweeter.client.observer.IUnfollowObserver;
import edu.byu.cs.tweeter.client.observer.PostStatusObserver;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter{
    private final View view;

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
    private UserService userService;
    private StatusService statusService;
    private FollowService followService;

    public MainPresenter(View view) {
        this.view = view;
    }

    private void enableFollowButton() {
        view.enableFollowButton();
    }

    private void displayErrorMessage(String message){
        view.displayErrorMessage(message);
    }
    private void displayInfoMessage(String message){
        view.displayInfoMessage(message);
    }

    public void initiateLogout(AuthToken currUserAuthToken) {
        getUserService().logout(currUserAuthToken, new LogoutObserver());
    }
    public void initiateGetFollowersAndFollowingCountTask(AuthToken currUserAuthToken, User user) {
        getFollowService().getFollowersAndFollowingCount(currUserAuthToken, user, new GetFollowersCountObserver(), new GetFollowingCountObserver());
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
    public void initiatePostStatus(AuthToken authToken, Status status){
        getStatusService().postStatus(authToken, status, new PostObserver());
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
        getFollowService().isFollower(authToken, user, selected, new IsFollowerObserver());
    }
    public void initiateUnfollow(AuthToken authToken, User user){
        getFollowService().unfollow(authToken, user, new UnfollowObserver());
    }
    public void initiateFollow(AuthToken authToken, User user){
        getFollowService().follow(authToken, user, new FollowObserver());
    }


    public UserService getUserService(){
        if (this.userService == null){
            this.userService = new UserService();
        }
        return this.userService;
    }
    public StatusService getStatusService(){
        if (this.statusService == null){
            this.statusService = new StatusService();
        }
        return this.statusService;
    }
    public FollowService getFollowService(){
        if (this.followService == null){
            this.followService = new FollowService();
        }
        return this.followService;
    }


    //Observers
    public class PostObserver implements PostStatusObserver {
        @Override
        public void handleSuccess() {
            view.displayInfoMessage("Successfully Posted!");
        }
        @Override
        public void handleFailure(String message) {
            displayErrorMessage("Failed to post status: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            displayErrorMessage("Failed to post status because of exception: " + exception.getMessage());
        }
    }
    public class UnfollowObserver implements IUnfollowObserver{
        @Override
        public void handleEnableFollowButton() {
            enableFollowButton();
        }
        @Override
        public void handleSuccess() {
            view.updateFollowingsFollowers(true);
        }
        @Override
        public void handleFailure(String message) {
            displayErrorMessage("Failed to unfollow: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            displayErrorMessage("Failed to unfollow due to exception: " + exception.getMessage());
        }
    }

    public class FollowObserver implements IFollowObserver{
        @Override
        public void handleEnableFollowButton() {
            enableFollowButton();
        }
        @Override
        public void handleSuccess() {
            view.updateFollowingsFollowers(false);
        }
        @Override
        public void handleFailure(String message) {
            displayErrorMessage("Failed to follow: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            displayErrorMessage("Failed to follow due to exception: " + exception.getMessage());
        }
    }

    public class LogoutObserver implements ILogoutObserver{
        @Override
        public void handleSuccess() {
            displayInfoMessage("Logging out...");
            view.logoutUser();
        }
        @Override
        public void handleFailure(String message) {
            displayErrorMessage("Failed to logout: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            displayErrorMessage("Failed to logout due to exception: " + exception.getMessage());
        }
    }

    public class IsFollowerObserver implements IIsFollowerObserver{
        @Override
        public void handleSuccess(boolean isFollower) {
            if (isFollower) {
                view.displayFollowingButton();
            } else {
                view.displayFollowButton();
            }
        }
        @Override
        public void handleFailure(String message) {
            displayErrorMessage("Failed while checking if is follower: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            displayErrorMessage("Failed while checking if is follower due to exception: " + exception.getMessage());
        }
    }

    public class GetFollowingCountObserver implements IGetFollowingCountObserver{
        @Override
        public void handleFollowingCountSuccess(int count) {
            view.displayFollowingCount(count);
        }
        @Override
        public void handleFailure(String message) {
            displayErrorMessage("Failed to get following count: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            displayErrorMessage("Failed to get following count due to exception: " + exception.getMessage());
        }
    }

    public class GetFollowersCountObserver implements IGetFollowersCountObserver{
        @Override
        public void handleFollowerCountSuccess(int count) {
            view.displayFollowerCount(count);
        }
        @Override
        public void handleFailure(String message) {
            displayErrorMessage("Failed to get following count: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            displayErrorMessage("Failed to get following count due to exception: " + exception.getMessage());
        }

    }

}
