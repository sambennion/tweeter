package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    private static final String LOG_TAG = "GetFollowingCountTask";

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() {
        try{
            FollowingCountRequest request = new FollowingCountRequest(authToken, getTargetUser().getAlias());
            FollowingCountResponse response = getServerFacade().getFollowingCount(request, FollowService.FOLLOWING_COUNT_URL_PATH);
            return response.getCount();

        } catch (Exception exception){
            Log.e(LOG_TAG, "Failed to get following count", exception);
            sendExceptionMessage(exception);
        }
        //if zero, we know something is wrong.
        return 0;
    }
}
