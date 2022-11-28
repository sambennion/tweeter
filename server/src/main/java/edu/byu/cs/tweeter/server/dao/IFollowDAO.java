package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;

public interface IFollowDAO{
    Integer getFolloweeCount(User follower);
    FollowingResponse getFollowees(FollowingRequest request);
    GetFollowersResponse getFollowers(GetFollowersRequest request);
    List<User> getDummyUsers();
}
