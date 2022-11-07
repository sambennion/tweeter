package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {

    private static final String LOG_TAG = "GetFollowingTask";

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollowee, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        Pair<List<User>, Boolean> result = new Pair<>(new ArrayList<>(), false);

        String lastItemAlias = null;
        if(getLastItem() != null){
            lastItemAlias = getLastItem().getAlias();
        }
        try {
            FollowingRequest request = new FollowingRequest(authToken, getTargetUser().getAlias(), getLimit(), lastItemAlias);
            FollowingResponse response = getServerFacade().getFollowees(request, FollowService.GET_FOLLOWING_URL_PATH);

            result.setFirst(response.getFollowees());
            result.setSecond(response.getHasMorePages());
        } catch (Exception exception){
            Log.e(LOG_TAG, "Failed to get followees", exception);
            sendExceptionMessage(exception);
        }
        return result;

    }
}
