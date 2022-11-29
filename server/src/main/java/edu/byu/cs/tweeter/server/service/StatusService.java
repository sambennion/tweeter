package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.bean.StoryBean;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService extends Service{

    public FeedResponse getFeed(FeedRequest request){
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

//        Pair<List, >
//        List<User> followees = getFollowDAO().getFollowees(request.getTargetUserAlias(), request.getLimit(), request.getLastStatus().getPost());


        Pair<List<Status>, Boolean> statusPair = getStatusDao().getFeed(request);
        List<Status> statuses = new ArrayList<>();
        //add users back in (they should just have alias's before this.
        for(Status status: statusPair.getFirst()){
            User user = getUserDao().getUserByAlias(status.getUser().getAlias());
            System.out.println("Setting user to " + user.toString());
            status.setUser(user);
            System.out.println("Adding Status " + status.toString());
            statuses.add(status);
        }
        return new FeedResponse(statuses, statusPair.getSecond());
//        List<Status> feed = getStatusDao().getFeed(request);
//        return getStatusDao().getFeed(request);
    }

    public StoryResponse getStory(StoryRequest request){
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        User targetUser = getUserDao().getUserByAlias(request.getTargetUserAlias());


        Pair<List<Status>, Boolean> story = getStatusDao().getStory(request, targetUser);


        return new StoryResponse(story.getFirst(), story.getSecond());
//        return getStatusDao().getStory(request, targetUser);
    }

    public PostStatusResponse postStatus(PostStatusRequest request){
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }
        else if(request.getStatus().post == ""){
            throw new RuntimeException("[Bad Request] Request needs to post string");
        }
        return getStatusDao().postStatus(request);
//        return new PostStatusResponse();
    }


}
