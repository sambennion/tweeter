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
import edu.byu.cs.tweeter.client.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.backgroundTask.handler.GetFolloweesHandler;
import edu.byu.cs.tweeter.client.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.backgroundTask.handler.GetFollowersHandler;
import edu.byu.cs.tweeter.client.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.backgroundTask.handler.UnfollowHandler;
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
}