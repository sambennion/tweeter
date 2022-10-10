package edu.byu.cs.tweeter.client.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.observer.FollowObserver;
import edu.byu.cs.tweeter.client.observer.GetFollowersCountObserver;
import edu.byu.cs.tweeter.client.observer.GetFollowersObserver;
import edu.byu.cs.tweeter.client.observer.GetFollowingCountObserver;
import edu.byu.cs.tweeter.client.observer.GetFollowingObserver;
import edu.byu.cs.tweeter.client.observer.IsFollowerObserver;
import edu.byu.cs.tweeter.client.observer.UnfollowObserver;
import edu.byu.cs.tweeter.client.presenter.FollowersPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service{



    /**
     * Creates an instance.
     */
    public FollowService() {}

    /**
     * Requests the users that the user specified in the request is following.
     * Limits the number of followees returned and returns the next set of
     * followees after any that were returned in a previous request.
     * This is an asynchronous operation.
     *
     * @param authToken the session auth token.
     * @param targetUser the user for whom followees are being retrieved.
     * @param limit the maximum number of followees to return.
     * @param lastFollowee the last followee returned in the previous request (can be null).
     */
    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetFollowingObserver observer) {
        runTask(new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetFolloweesHandler(observer)));
    }
    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetFollowersObserver observer) {
        runTask(new GetFollowersTask(authToken, targetUser, limit, lastFollower, new GetFollowersHandler(observer)));
    }
    public void isFollower(AuthToken authToken, User user, User selected, IsFollowerObserver observer){
        runTask(new IsFollowerTask(authToken, user, selected, new IsFollowerHandler(observer)));
    }
    public void unfollow(AuthToken authToken, User user, UnfollowObserver observer){
        runTask(new UnfollowTask(authToken,
                user, new UnfollowHandler(observer)));
    }
    public void follow(AuthToken authToken, User user, FollowObserver observer){
        runTask(new FollowTask(authToken,
                user, new FollowHandler(observer)));
    }
    public void getFollowersAndFollowingCount(AuthToken authToken, User user, GetFollowersCountObserver observer1, GetFollowingCountObserver observer2){
        runTwoTasks(new GetFollowersCountTask(authToken,
                user, new GetFollowersCountHandler(observer1)), new GetFollowingCountTask(authToken,
                user, new GetFollowingCountHandler(observer2)));
    }


    /**
     * Handles messages from the background task indicating that the task is done, by invoking
     * methods on the observer.
     */
    public static class GetFolloweesHandler extends Handler {

        private final GetFollowingObserver observer;

        public GetFolloweesHandler(GetFollowingObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            boolean success = bundle.getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) bundle.getSerializable(GetFollowingTask.ITEMS_KEY);
                boolean hasMorePages = bundle.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                observer.handleSuccess(followees, hasMorePages);
            } else if (bundle.containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String errorMessage = bundle.getString(GetFollowingTask.MESSAGE_KEY);
                observer.handleFailure(errorMessage);
            } else if (bundle.containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) bundle.getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }

    }

    public static class GetFollowersHandler extends Handler {

        private final GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver observer) {
            super(Looper.getMainLooper());
            this.observer = observer;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            boolean success = bundle.getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) bundle.getSerializable(GetFollowersTask.ITEMS_KEY);
                boolean hasMorePages = bundle.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                observer.handleSuccess(followers, hasMorePages);
            } else if (bundle.containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String errorMessage = bundle.getString(GetFollowersTask.MESSAGE_KEY);
                observer.handleFailure(errorMessage);
            } else if (bundle.containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) bundle.getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }

    }

    private class IsFollowerHandler extends Handler {

        private final IsFollowerObserver observer;

        public IsFollowerHandler(IsFollowerObserver observer){
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(IsFollowerTask.SUCCESS_KEY);
            if (success) {
                boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
                observer.handleSuccess(isFollower);
            } else if (msg.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(IsFollowerTask.MESSAGE_KEY);
                observer.handleFailure("Failed to determine following relationship: " + message);
            } else if (msg.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }
    }
    // FollowHandler

    private class FollowHandler extends Handler {
        private FollowObserver observer;
        public FollowHandler(FollowObserver observer){this.observer = observer;}

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                observer.handleFollowSuccess();
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
                //"Failed to follow: "
                observer.handleFailure(message);
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
            observer.handleEnableFollowButton();
        }
    }

    // UnfollowHandler

    private class UnfollowHandler extends Handler {
        private UnfollowObserver observer;
        public UnfollowHandler(UnfollowObserver observer){this.observer = observer;}
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
            if (success) {
                observer.handleUnfollowSuccess();
            } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                observer.handleFailure(message);
            } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
            observer.handleEnableFollowButton();
        }
    }
    private class GetFollowersCountHandler extends Handler {
        private GetFollowersCountObserver observer;
        public GetFollowersCountHandler(GetFollowersCountObserver observer){this.observer = observer;}
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                observer.handleFollowerCountSuccess(count);
            } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.handleFailure("Failed to get followers count: " + message);
            } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }
    }

    // GetFollowingCountHandler

    private class GetFollowingCountHandler extends Handler {
        private GetFollowingCountObserver observer;
        public GetFollowingCountHandler(GetFollowingCountObserver observer){this.observer = observer;}
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
                observer.handleFollowingCountSuccess(count);
            } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                observer.handleFailure("Failed to get following count: " + message);
            } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }
    }

}