package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDao;
import edu.byu.cs.tweeter.server.dao.UserDao;
import edu.byu.cs.tweeter.server.dao.bean.Follows;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service{

    public FollowService(IFollowDAO followDAO, IStatusDAO statusDAO, IUserDao userDao) {
        super(followDAO, statusDAO, userDao);
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        Pair<List<User>, Boolean> followeesPair = followDAO.getFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
        List<User> followees = convertAliasUsersToFullUsers(followeesPair.getFirst());
        return new FollowingResponse(followees, followeesPair.getSecond());
//        List<Follows> follows = getFollowDAO().getFollowees(request);
//        List<User> users = new ArrayList();
//        for(Follows follow: follows){
//            users.add(getUserDao().getUserByAlias(follow.getFollowee_handle()));
//        }
//
//        return new FollowingResponse(users, );
    }



    public GetFollowersResponse getFollowers(GetFollowersRequest request){
        if(request.getFolloweeAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        Pair<List<User>, Boolean> followersPair = followDAO.getFollowers(request.getFolloweeAlias(), request.getLimit(), request.getLastFollowerAlias());
        List<User> followers = convertAliasUsersToFullUsers(followersPair.getFirst());
        return new GetFollowersResponse(followers, followersPair.getSecond());
    }

    public FollowResponse follow(FollowRequest request){

        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        followDAO.follow(request);
        userDao.incrementFollowingCount(request.getFollowerAlias());
        userDao.incrementFollowerCount(request.getFolloweeAlias());
        return new FollowResponse();
//        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request){
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        System.out.println("Starting unfollow");
        followDAO.unfollow(request);
        System.out.println("Decrementing following count of follower");
        userDao.decrementFollowingCount(request.getFollowerAlias());
        System.out.println("Decrementing follower count of followee");
        userDao.decrementFollowerCount(request.getFolloweeAlias());

        return new UnfollowResponse();
//        return new UnfollowResponse();
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request){
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        else if(request.getFollowee() == null || request.getFollower() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a follower and followee");
        }
        boolean isFollower = followDAO.isFollower(request);
        if(isFollower){
            System.out.println("Sending isFollower is true message");
        }
        else{
            System.out.println("Sending isFollower is false message");
        }
        return new IsFollowerResponse(isFollower);
//        return new IsFollowerResponse(true);
    }

    private List<User> convertAliasUsersToFullUsers(List<User> users){
        List<User> newUsers = new ArrayList<>();
        for(User user : users){
            user = userDao.getUserByAlias(user.getAlias());
            newUsers.add(user);
        }
        return newUsers;
    }

}
