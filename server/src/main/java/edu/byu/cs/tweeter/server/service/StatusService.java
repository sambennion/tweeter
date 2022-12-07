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
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDao;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.bean.StoryBean;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService extends Service{


    public StatusService(IFollowDAO followDAO, IStatusDAO statusDAO, IUserDao userDao) {
        super(followDAO, statusDAO, userDao);
    }

    public FeedResponse getFeed(FeedRequest request){
        if(request.getAuthToken() == null ||  isExpiredAuthToken(request.getAuthToken().getDatetime())){
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }



        Pair<List<Status>, Boolean> statusPair = statusDAO.getFeed(request);
        List<Status> statuses = new ArrayList<>();
        //add users back in (they should just have alias's before this.
        for(Status status: statusPair.getFirst()){
            User user = userDao.getUserByAlias(status.getUser().getAlias());
            System.out.println("Setting user to " + user.toString());
            status.setUser(user);
            System.out.println("Adding Status " + status.toString());
            statuses.add(status);
        }
        return new FeedResponse(statuses, statusPair.getSecond());
    }

    public StoryResponse getStory(StoryRequest request){
        if(request.getAuthToken() == null ||  isExpiredAuthToken(request.getAuthToken().getDatetime())) {
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        User targetUser = userDao.getUserByAlias(request.getTargetUserAlias());


        Pair<List<Status>, Boolean> story = statusDAO.getStory(request, targetUser);


        return new StoryResponse(story.getFirst(), story.getSecond());
//        return getStatusDao().getStory(request, targetUser);
    }

    public PostStatusResponse postStatus(PostStatusRequest request){
        if(request.getAuthToken() == null ||  isExpiredAuthToken(request.getAuthToken().getDatetime())){
            throw new RuntimeException("[Bad Request] Request needs to have an authtoken");
        }
        else if(request.getStatus().post == ""){
            throw new RuntimeException("[Bad Request] Request needs to post string");
        }
        List<User> followers = new ArrayList<>();
//        boolean hasMoreFollowers = true;
//        String lastFollower = null;
//        while(hasMoreFollowers == true){
//            Pair<List<User>, Boolean> followerBatch = followDAO.getFollowers(request.getStatus().getUser().getAlias(), 10, lastFollower);
//            for(User user : followerBatch.getFirst()){
//                followers.add(user);
//            }
//            hasMoreFollowers = followerBatch.getSecond();
//            lastFollower = followerBatch.getFirst().get(followerBatch.getFirst().size()-1).getAlias();
//        }
        statusDAO.postStatus(request, followers);
        return new PostStatusResponse();
//        return new PostStatusResponse();
    }


}
