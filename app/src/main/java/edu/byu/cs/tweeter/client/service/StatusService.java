package edu.byu.cs.tweeter.client.service;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.backgroundTask.handler.GetStatusesHandler;
import edu.byu.cs.tweeter.client.backgroundTask.handler.PostStatusHandler;
import edu.byu.cs.tweeter.client.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.client.observer.PostStatusObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service{

    public void getStory(AuthToken authToken, User user, int limit, Status lastStatus, GetStatusesObserver observer){
        runTask(new GetStoryTask(authToken,
                user, limit, lastStatus, new GetStatusesHandler(observer)));
    }

    public void getFeed(AuthToken authToken, User user, int limit, Status lastStatus, GetStatusesObserver observer){
        runTask(new GetFeedTask(authToken,
                user, limit, lastStatus, new GetStatusesHandler(observer)));
    }
    public void postStatus(AuthToken authToken, Status status, PostStatusObserver observer){
        runTask(new PostStatusTask(authToken,
                status, new PostStatusHandler(observer)));
    }
}
