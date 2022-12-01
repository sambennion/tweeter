package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.util.Pair;

public interface IStatusDAO {

    Pair<List<Status>, Boolean> getFeed(FeedRequest request);

    Pair<List<Status>, Boolean> getStory(StoryRequest request, User targetUser);

    void postStatus(PostStatusRequest request, List<User> followers);

    void deleteFeedMember(String alias);
}
