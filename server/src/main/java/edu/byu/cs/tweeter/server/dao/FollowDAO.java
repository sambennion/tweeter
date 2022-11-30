package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.server.dao.bean.Follows;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO extends Dao implements IFollowDAO {

    private UserDao userDao;
        private static final String TableName = "follows";
        public static final String IndexName = "follows_index";

        private static final String FollowerHandleAttr = "follower_handle";
        private static final String FolloweeHandleAttr = "followee_handle";
    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *

     */
    @Override
    public Pair<List<User>, Boolean> getFollowees(String followerAlias, int limit, String lastFollowee) {
        List<Follows> follows = getFolloweeBeans(followerAlias, limit, lastFollowee);
        List<User> responseFollowees = new ArrayList<>(limit);
        boolean hasMorePages = false;
        System.out.println(follows.size());

        for(Follows follow: follows){
            //Add user. Needs to get user first.
            User followee = new User(follow.getFollowee_handle());
            System.out.println("Adding followee " + followee.getAlias());
            responseFollowees.add(followee);
        }
        hasMorePages = limit == responseFollowees.size();
        return new Pair<>(responseFollowees, hasMorePages);
//        return new FollowingResponse(responseFollowees, hasMorePages);

    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.

     *                other information required to satisfy the request.
     * @return the followees.
     */
    @Override
    public Pair<List<User>, Boolean> getFollowers(String followee_handle, int pageSize, String lastFollower) {
        List<Follows> follows = getFolloweeBeans(followee_handle, pageSize, lastFollower);
        List<User> responseFollowers = new ArrayList<>(pageSize);
        boolean hasMorePages = false;
        System.out.println(follows.size());

        for(Follows follow: follows){
            //Add user. Needs to get user first.
            User follower = new User(follow.getFollowee_handle());
            System.out.println("Adding followee " + follower.getAlias());
            responseFollowers.add(follower);
        }
        hasMorePages = pageSize == responseFollowers.size();
        return new Pair<>(responseFollowers, hasMorePages);
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    private int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }

        return followersIndex;
    }

    @Override
    public void follow(FollowRequest request) {
        System.out.println(request.toString());
        System.out.println(request.getFollowerAlias() + " following " + request.getFolloweeAlias());

        setFollow(request.getFollowerAlias(), request.getFolloweeAlias(), "", "");
    }
    @Override
    public void unfollow(UnfollowRequest request) {
        System.out.println("Removing follow from db");
        deleteFollow(request.getFollowerAlias(), request.getFolloweeAlias());
//        return new UnfollowResponse();
    }
    @Override
    public boolean isFollower(IsFollowerRequest request) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(request.getFollower().getAlias()).sortValue(request.getFollowee().getAlias())
                .build();

        Follows follows = table.getItem(key);
        boolean isFollower = false;
        if(follows != null){
            isFollower = true;
        }
        System.out.println("isFollower = True for " + request.getFollower());
        return isFollower;
    }







    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    private void setFollow(String follower_handle, String followee_handle, String follower_name, String followee_name) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        System.out.println("Selected table");
        System.out.println("Building Key...");
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();
        System.out.println("Created key");

        // load it if it exists
        Follows follows = table.getItem(key);
        if(follows != null) {
            System.out.println("Follows not null");
            if(follower_name != ""){
                follows.setFollower_name(follower_name);
            }
            if(followee_name != ""){
                follows.setFollowee_name(followee_name);
            }
            table.updateItem(follows);
        } else {
            System.out.println("Follows null");
            Follows newFollows = new Follows();
            newFollows.setFollower_handle(follower_handle);
            System.out.println("Set follower");
            newFollows.setFollowee_handle(followee_handle);
            System.out.println("Set Followee");
            //Don't overwrite name if only setting follower_name or followee_name
            if(follower_name != ""){
                newFollows.setFollower_name(follower_name);
            }
            if(followee_name != ""){
                newFollows.setFollowee_name(followee_name);
            }
            System.out.println("Adding follow to table");
            System.out.println(newFollows.getFollower_handle() + "->" + newFollows.getFollowee_handle());
            table.putItem(newFollows);
        }
    }

    private void updateFollowerHandle(String currentFollowerHandle, String followeeHandle, String replacementFollowerHandle) throws Exception {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(currentFollowerHandle).sortValue(followeeHandle)
                .build();

        // load it if it exists
        Follows follows = table.getItem(key);

        if(follows != null){
            follows.setFollower_handle(replacementFollowerHandle);
            table.updateItem(follows);
        }
        else{
            throw new Exception("Update exception: Table with that follower_handle does not exist.");
        }
    }
    private void updateFolloweeHandle(String currentFollowerHandle, String followeeHandle, String replacementFolloweeHandle) throws Exception {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(currentFollowerHandle).sortValue(followeeHandle)
                .build();

        // load it if it exists
        Follows follows = table.getItem(key);

        if(follows != null){
            follows.setFollowee_handle(replacementFolloweeHandle);
            table.updateItem(follows);
        }
        else{
            throw new Exception("Update exception: Table with that follower_handle does not exist.");
        }
    }

    /**
     * Delete the follow in relationship follower_handle -> followee_handle
     *
     * @param follower_handle
     * @param followee_handle
     */
    private void deleteFollow(String follower_handle, String followee_handle) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        System.out.println("Deleting follow " + follower_handle + "->" + followee_handle);
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();
        table.deleteItem(key);
    }

    /**
     * Fetch the next page of locations visited by visitor
     *
     * @param follower_handle The follower of interest
     * @param pageSize The maximum number of follows to include in the result
     * @param lastFollowee The last followee returned in the previous page of results
     * @return The next page of follows
     */
    private List<Follows> getFolloweeBeans(String follower_handle, int pageSize, String lastFollowee) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(follower_handle)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));
        // If you use iterators, it auto-fetches next page always, so instead limit the stream below
        //.limit(5);

        if(isNonEmptyString(lastFollowee)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerHandleAttr, AttributeValue.builder().s(follower_handle).build());
            startKey.put(FolloweeHandleAttr, AttributeValue.builder().s(lastFollowee).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        return table.query(request)
                .items()
                .stream()
                .limit(pageSize)
                .collect(Collectors.toList());

        // an alternative implementation of the line above
        //List<Visit> visits = new ArrayList<>();
        //table.query(request).items().stream().limit(pageSize).forEach(v -> visits.add(v));
        //return visits;
    }



    /**
     * Fetch the next page of follows who have followed the followee location
     *
     * @param followee_handle The followee of interest
     * @param pageSize The maximum number of follows to include in the result
     * @param lastFollower The last follower returned in the previous page of results
     * @return The next page of follows of the followee
     */
    private List<Follows> getFollowerBeans(String followee_handle, int pageSize, String lastFollower) {
        DynamoDbIndex<Follows> index = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class)).index(IndexName);
        Key key = Key.builder()
                .partitionValue(followee_handle)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                // Unlike Tables, querying from an Index returns a PageIterable, so we want to just ask for
                // 1 page with pageSize items
                .limit(pageSize);

        if(isNonEmptyString(lastFollower)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeHandleAttr, AttributeValue.builder().s(followee_handle).build());
            startKey.put(FollowerHandleAttr, AttributeValue.builder().s(lastFollower).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<Follows> follows = new ArrayList<>();

        SdkIterable<Page<Follows>> results2 = index.query(request);
        PageIterable<Follows> pages = PageIterable.create(results2);
        // limit 1 page, with pageSize items
        pages.stream()
                .limit(1)
                .forEach(visitsPage -> visitsPage.items().forEach(v -> follows.add(v)));

        return follows;
    }
}
