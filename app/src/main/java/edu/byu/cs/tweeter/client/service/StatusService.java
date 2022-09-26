package edu.byu.cs.tweeter.client.service;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {


    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface GetStatusesObserver {
        void handleSuccess(List<Status> statues, boolean hasMorePages);
        void handleFailure(String message);
        void handleException(Exception exception);
    }
    public interface PostStatusObserver {
        void handleSuccess();
        void handleFailure(String message);
        void handleException(Exception exception);
    }


    public void getStory(AuthToken authToken, User user, int limit, Status lastStatus, GetStatusesObserver observer){
        GetStoryTask getStoryTask = new GetStoryTask(authToken,
                        user, limit, lastStatus, new GetStatusesHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getStoryTask);
    }

    public void getFeed(AuthToken authToken, User user, int limit, Status lastStatus, GetStatusesObserver observer){
        GetFeedTask getFeedTask = new GetFeedTask(authToken,
                user, limit, lastStatus, new GetStatusesHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFeedTask);
    }
    public void postStatus(AuthToken authToken, Status status, PostStatusObserver observer){
        PostStatusTask statusTask = new PostStatusTask(authToken,
                status, new PostStatusHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(statusTask);
    }

    private class GetStatusesHandler extends Handler {
        GetStatusesObserver observer;
        public GetStatusesHandler(GetStatusesObserver observer){this.observer = observer;}
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetStoryTask.SUCCESS_KEY);
            if (success) {

                List<Status> statuses = (List<Status>) msg.getData().getSerializable(GetStoryTask.STATUSES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetStoryTask.MORE_PAGES_KEY);
                observer.handleSuccess(statuses, hasMorePages);
            } else if (msg.getData().containsKey(GetStoryTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetStoryTask.MESSAGE_KEY);
                observer.handleFailure(message);
            } else if (msg.getData().containsKey(GetStoryTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetStoryTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }
    }
    private class PostStatusHandler extends Handler {
        private PostStatusObserver observer;
        public PostStatusHandler(PostStatusObserver observer){this.observer = observer;}
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(PostStatusTask.SUCCESS_KEY);
            if (success) {
                observer.handleSuccess();
            } else if (msg.getData().containsKey(PostStatusTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(PostStatusTask.MESSAGE_KEY);
                observer.handleFailure(message);
            } else if (msg.getData().containsKey(PostStatusTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(PostStatusTask.EXCEPTION_KEY);
                observer.handleException(ex);
            }
        }
    }
}
