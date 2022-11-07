package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    private static final String LOG_TAG = "GetStoryTask";

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        Pair<List<Status>, Boolean> result = new Pair<>(new ArrayList<>(), false);

        try {
            StoryRequest request = new StoryRequest(authToken, getTargetUser().getAlias(), getLimit(), getLastItem());
            StoryResponse response = getServerFacade().getStory(request, StatusService.STORY_URL_PATH);

            result.setFirst(response.getStatuses());
            result.setSecond(response.getHasMorePages());
        } catch (Exception exception){
            Log.e(LOG_TAG, "Failed to get story", exception);
            sendExceptionMessage(exception);
        }
        return result;
    }
}
