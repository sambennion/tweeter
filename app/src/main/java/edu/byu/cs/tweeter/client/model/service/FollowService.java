package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFolloweesHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowersHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.UnfollowHandler;
import edu.byu.cs.tweeter.client.observer.GetFollowingObserver;
import edu.byu.cs.tweeter.client.observer.IFollowObserver;
import edu.byu.cs.tweeter.client.observer.IGetFollowersCountObserver;
import edu.byu.cs.tweeter.client.observer.IGetFollowersObserver;
import edu.byu.cs.tweeter.client.observer.IGetFollowingCountObserver;
import edu.byu.cs.tweeter.client.observer.IIsFollowerObserver;
import edu.byu.cs.tweeter.client.observer.IUnfollowObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service{

    public static final String GET_FOLLOWING_URL_PATH = "/getfollowing";
    public static final String GET_FOLLOWERS_URL_PATH = "/getfollowers";
    public static final String FOLLOWING_COUNT_URL_PATH = "/followingcount";
    public static final String FOLLOWERS_COUNT_URL_PATH = "/followerscount";
    public static final String IS_FOLLOWER_URL_PATH = "/isfollower";
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
    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, IGetFollowersObserver observer) {
        runTask(new GetFollowersTask(authToken, targetUser, limit, lastFollower, new GetFollowersHandler(observer)));
    }
    public void isFollower(AuthToken authToken, User user, User selected, IIsFollowerObserver observer){
        runTask(new IsFollowerTask(authToken, user, selected, new IsFollowerHandler(observer)));
    }
    public void unfollow(AuthToken authToken, User user, IUnfollowObserver observer){
        runTask(new UnfollowTask(authToken,
                user, new UnfollowHandler(observer)));
    }
    public void follow(AuthToken authToken, User user, IFollowObserver observer){
        runTask(new FollowTask(authToken,
                user, new FollowHandler(observer)));
    }
    public void getFollowersAndFollowingCount(AuthToken authToken, User user, IGetFollowersCountObserver observer1, IGetFollowingCountObserver observer2){
        runTwoTasks(new GetFollowersCountTask(authToken,
                user, new GetFollowersCountHandler(observer1)), new GetFollowingCountTask(authToken,
                user, new GetFollowingCountHandler(observer2)));
    }
}