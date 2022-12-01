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
    private static final String FeedOwnerHandleAttribute = "alias";
    private static final String StatusAttribute = "status";


    @Override
    public Pair<List<Status>, Boolean> getFeed(FeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getAuthToken() != null;
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
    public void postStatus(PostStatusRequest request, List<User> followers) {
        Status status = request.getStatus();

        postToStory(status);

        postToFeed(status, followers);



//        return new PostStatusResponse();
    }

    @Override
    public void deleteFeedMember(String alias){
//        DynamoDbTable<FeedBean> table = getFeedTable();

    }

    private void postToStory(Status status){
        System.out.println("Selecting table and building key");
        DynamoDbTable<StoryBean> storyTable = getStoryTable();
        Key key = buildKey(status.getUser().getAlias());
        System.out.println("Creating story bean");
        StoryBean storyBean = new StoryBean();
        storyBean.setAlias(status.getUser().getAlias());
        storyBean.setTimestamp(status.getDate());
        storyBean.setMentions(status.getMentions());
        storyBean.setStatus(status.getPost());
        storyBean.setUrls(status.getUrls());

        System.out.println("running putItem on story bean");
        storyTable.putItem(storyBean);
    }


    private void postToFeed(Status status, List<User> followers){
        DynamoDbTable<FeedBean> feedTable = getFeedTable();

        for(User user : followers){
            Key key = buildKey(user.getAlias());
            FeedBean feedBean = new FeedBean();
            feedBean.setStatus(status.getPost());
            feedBean.setAlias(user.getAlias());
            feedBean.setStatusOwner(status.getUser().getAlias());
            feedBean.setMentions(status.getMentions());
            feedBean.setTimestamp(status.getDate());
            feedBean.setUrls(status.getUrls());
            System.out.println("Adding item to feed table");
            feedTable.putItem(feedBean);
        }
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

    private DynamoDbTable<StoryBean> getStoryTable(){
        return enhancedClient.table(StoryTableName, TableSchema.fromBean(StoryBean.class));
    }

    private DynamoDbTable<FeedBean> getFeedTable(){
        return enhancedClient.table(FeedTableName, TableSchema.fromBean(FeedBean.class));
    }
}
