package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.bean.FeedBean;
import edu.byu.cs.tweeter.server.dao.bean.StoryBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StatusDAO extends Dao implements IStatusDAO {

    private static final String StoryTableName = "story";
    private static final String FeedTableName = "feed";
//    public static final String IndexName = "follows_index";
    private static final String FeedOwnerHandleAttribute = "alias";
    private static final String StatusAttribute = "status";


    @Override
    public Pair<List<Status>, Boolean> getFeed(FeedRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        System.out.println("Request for feed for alias " + request.getTargetUserAlias() + " limit = " + request.getLimit() + " last status = " + request.getLastStatus());
        String lastStatus = null;
        if(request.getLastStatus() != null){
            lastStatus = request.getLastStatus().toString();
        }
        List<FeedBean> feedBeans = getFeedBeans(request.getTargetUserAlias(), request.getLimit(), lastStatus);
        List<Status> feed = new ArrayList<>(request.getLimit());

        System.out.println("In getFeed -> StatusDAO");
        boolean hasMorePages = false;
//        System.out.println(feedBeans.size());
//        return Pair<>
        for(FeedBean feedBean: feedBeans){
            //Add user. Needs to get user first.
            Status status = new Status(feedBean.getStatus(), new User(feedBean.getStatusOwner()), feedBean.getTimestamp(), feedBean.getUrls(), feedBean.getMentions());
            System.out.println("Adding status");
            feed.add(status);
        }
        hasMorePages = request.getLimit() == feed.size();
        return new Pair<>(feed, hasMorePages);



//        assert request.getLimit() > 0;
//        assert request.getAuthToken() != null;
//
//
//        List<Status> allStatuses = getDummyStatuses();
//        List<Status> responseStatuses = new ArrayList<>(request.getLimit());
//
//        boolean hasMorePages = false;
//
//        if(request.getLimit() > 0) {
//            if (allStatuses != null) {
//                int statusesIndex = getStatusesStartingIndex(request.getLastStatus(), allStatuses);
//
//                for(int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < request.getLimit(); statusesIndex++, limitCounter++) {
//                    responseStatuses.add(allStatuses.get(statusesIndex));
//                }
//
//                hasMorePages = statusesIndex < allStatuses.size();
//            }
//        }
//
//        return new Pair<>(responseStatuses, hasMorePages);
//        return new FeedResponse(responseStatuses, hasMorePages);



//        return getFakeData().getFakeStatuses();
    }

    @Override
    public Pair<List<Status>, Boolean> getStory(StoryRequest request, User targetUser) {
        String lastStatus = null;
        if(request.getLastStatus() != null){
            lastStatus = request.getLastStatus().toString();
        }
        List<StoryBean> storyBeans = getStoryBeans(request.getTargetUserAlias(), request.getLimit(), lastStatus);

//        return new Pair(storyBeans ,request.getLimit() == storyBeans.size());
        List<Status> story = new ArrayList<>(request.getLimit());


        boolean hasMorePages = false;
        System.out.println(storyBeans.size());
//        return Pair<>
        for(StoryBean storyBean: storyBeans){
            //Add user. Needs to get user first.
            Status status = new Status(storyBean.getStatus(), targetUser, storyBean.getTimestamp(), storyBean.getUrls(), storyBean.getMentions());
            System.out.println("Adding status");
            story.add(status);
        }
        hasMorePages = request.getLimit() == story.size();
        return new Pair<>(story, hasMorePages);


//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert request.getLimit() > 0;
//        assert request.getAuthToken() != null;
//
//        List<Status> allStatuses = getDummyStatuses();
//        List<Status> responseStatuses = new ArrayList<>(request.getLimit());
//
//        boolean hasMorePages = false;
//
//        if(request.getLimit() > 0) {
//            if (allStatuses != null) {
//                int statusesIndex = getStatusesStartingIndex(request.getLastStatus(), allStatuses);
//
//                for(int limitCounter = 0; statusesIndex < allStatuses.size() && limitCounter < request.getLimit(); statusesIndex++, limitCounter++) {
//                    responseStatuses.add(allStatuses.get(statusesIndex));
//                }
//
//                hasMorePages = statusesIndex < allStatuses.size();
//            }
//        }
//
//        return new StoryResponse(responseStatuses, hasMorePages);

    }

    private int getStatusesStartingIndex(Status lastStatus, List<Status> allStatuses) {

        int statusesIndex = 0;

        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatuses.size(); i++) {
                if(lastStatus.equals(allStatuses.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusesIndex = i + 1;
                    break;
                }
            }
        }

        return statusesIndex;
    }

    @Override
    public List<Status> getDummyStatuses() {
        return getFakeData().getFakeStatuses();
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        return new PostStatusResponse();
    }




    /**
     * Fetch the next page of locations visited by visitor
     *
     * @return The next page of follows
     */
    private List<StoryBean> getStoryBeans(String alias, int pageSize, String lastStatus) {
        DynamoDbTable<StoryBean> table = enhancedClient.table(StoryTableName, TableSchema.fromBean(StoryBean.class));
        System.out.println("Selected Table");
        System.out.println("Building Key");
        Key key = Key.builder()
                .partitionValue(alias)
                .build();
        System.out.println("Building Request");
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));
        // If you use iterators, it auto-fetches next page always, so instead limit the stream below
        //.limit(5);
        System.out.println("Checking if last status == null or \"\"");
        if(lastStatus != "" && lastStatus != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FeedOwnerHandleAttribute, AttributeValue.builder().s(alias).build());
            startKey.put(StatusAttribute, AttributeValue.builder().s(lastStatus).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        return table.query(request)
                .items()
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    private List<FeedBean> getFeedBeans(String alias, int pageSize, String lastStatus) {
        System.out.println("Selecting table");
        DynamoDbTable<FeedBean> table = enhancedClient.table(FeedTableName, TableSchema.fromBean(FeedBean.class));
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));
        // If you use iterators, it auto-fetches next page always, so instead limit the stream below
        //.limit(5);

        System.out.println("Checking that lastStatus != \"\"");
        if(lastStatus != "" && lastStatus != null) {
            System.out.println("Last status exists");
            System.out.println("Setting start key");
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FeedOwnerHandleAttribute, AttributeValue.builder().s(alias).build());
            startKey.put(StatusAttribute, AttributeValue.builder().s(lastStatus).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        return table.query(request)
                .items()
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList());

    }

//    private Status convertStoryBeanToStatus(StoryBean storyBean){
//        return new Status(storyBean.getStatus(), storyBean.getAlias(), storyBean.getTimestamp(), storyBean.getUrl(), storyBean.getMentions());
//    }
}
