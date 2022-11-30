package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.util.Pair;

public interface IFollowDAO{
    void follow(FollowRequest request);

    void unfollow(UnfollowRequest request);
    Pair<List<User>, Boolean> getFollowees(String followerAlias, int limit, String lastFollowee);
    Pair<List<User>, Boolean> getFollowers(String followee_handle, int pageSize, String lastFollower);

    boolean isFollower(IsFollowerRequest request);
}
