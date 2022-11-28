package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    private static final String LOG_TAG = "GetFollowersCountTask";

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() {
        try{
            FollowersCountRequest request = new FollowersCountRequest(authToken, getTargetUser().getAlias());
            FollowersCountResponse response = getServerFacade().getFollowersCount(request, FollowService.FOLLOWERS_COUNT_URL_PATH);
            return response.getCount();

        } catch (Exception exception){
            Log.e(LOG_TAG, "Failed to get followers count", exception);
            sendExceptionMessage(exception);
        }
        //if zero, we know something is wrong.
        return 0;
    }
}
