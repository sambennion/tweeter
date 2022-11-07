package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {
    private static final String LOG_TAG = "GetFeedTask";

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        Pair<List<Status>, Boolean> result = new Pair<>(new ArrayList<>(), false);
        try {
            FeedRequest request = new FeedRequest(authToken, getTargetUser().getAlias(), getLimit(), getLastItem());
            FeedResponse response = getServerFacade().getFeed(request, StatusService.FEED_URL_PATH);

            result.setFirst(response.getStatuses());
            result.setSecond(response.getHasMorePages());
        } catch (Exception exception){
            Log.e(LOG_TAG, "Failed to get feed", exception);
            sendExceptionMessage(exception);
        }
        return result;
    }
}
