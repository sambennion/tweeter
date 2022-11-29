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
import edu.byu.cs.tweeter.server.dao.UserDao;
import edu.byu.cs.tweeter.server.dao.bean.Follows;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

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
        return getFollowDAO().getFollowees(request);
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
        return getFollowDAO().getFollowers(request);
    }

    public FollowResponse follow(FollowRequest request){

        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        return getFollowDAO().follow(request);
//        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request){
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        return getFollowDAO().unfollow(request);
//        return new UnfollowResponse();
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request){
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        else if(request.getFollowee() == null || request.getFollower() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a follower and followee");
        }
        return getFollowDAO().isFollower(request);
//        return new IsFollowerResponse(true);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowDAO() {
        return new FollowDAO();
    }
    UserDao getUserDao(){return new UserDao();}
}
