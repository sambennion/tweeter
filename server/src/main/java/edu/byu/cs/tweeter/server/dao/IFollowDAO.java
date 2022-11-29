package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.util.Pair;

public interface IFollowDAO{
    int getFolloweeCount(User follower);
    Pair<List<User>, Boolean> getFollowees(String followerAlias, int limit, String lastFollowee);
    GetFollowersResponse getFollowers(GetFollowersRequest request);
    List<User> getDummyUsers();
}
