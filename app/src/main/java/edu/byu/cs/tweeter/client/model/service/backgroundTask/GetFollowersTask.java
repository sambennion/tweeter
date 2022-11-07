package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    private static final String LOG_TAG = "GetFollowersTask";

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        Pair<List<User>, Boolean> result = new Pair<>(new ArrayList<>(), false);

        String lastItemAlias = null;
        if(getLastItem() != null){
            lastItemAlias = getLastItem().getAlias();
        }
        try {
            GetFollowersRequest request = new GetFollowersRequest(authToken, getTargetUser().getAlias(), getLimit(), lastItemAlias);
            GetFollowersResponse response = getServerFacade().getFollowers(request, FollowService.GET_FOLLOWERS_URL_PATH);

            result.setFirst(response.getFollowers());
            result.setSecond(response.getHasMorePages());
        } catch (Exception exception){
            Log.e(LOG_TAG, "Failed to get followers", exception);
            sendExceptionMessage(exception);
        }
        return result;
//        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
    }
}
