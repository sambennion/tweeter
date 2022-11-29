package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.bean.Follows;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
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
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param follower the User whose count of how many following is desired.
     * @return said count.
     */
    @Override
    public int getFolloweeCount(User follower) {
        // TODO: uses the dummy data.  Replace with a real implementation.
        assert follower != null;
        return getDummyUsers().size();
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    @Override
    public FollowingResponse getFollowees(FollowingRequest request) {
        List<Follows> follows = getFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
        List<User> responseFollowees = new ArrayList<>(request.getLimit());
        boolean hasMorePages = false;
        System.out.println(follows.size());

        for(Follows follow: follows){
            //Add user. Needs to get user first.
            User followee = getUserDao().getUserByAlias(follow.getFollowee_handle());
            System.out.println("Adding followee " + followee.getAlias());
            responseFollowees.add(followee);
        }
        hasMorePages = request.getLimit() == responseFollowees.size();
        return new FollowingResponse(responseFollowees, hasMorePages);






//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert request.getLimit() > 0;
//        assert request.getFollowerAlias() != null;
//
//        List<User> allFollowees = getDummyUsers();
//        List<User> responseFollowees = new ArrayList<>(request.getLimit());
//
//        boolean hasMorePages = false;
//
//        if(request.getLimit() > 0) {
//            if (allFollowees != null) {
//                int followeesIndex = getFolloweesStartingIndex(request.getLastFolloweeAlias(), allFollowees);
//
//                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
//                    responseFollowees.add(allFollowees.get(followeesIndex));
//                }
//
//                hasMorePages = followeesIndex < allFollowees.size();
//            }
//        }
//
////        return getFollowees(request.getFollowerAlias(), 10, null);
//
//        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    @Override
    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getFolloweeAlias() != null;

        List<User> allFollowers = getDummyUsers();
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowers != null) {
                int followersIndex = getFollowersStartingIndex(request.getLastFollowerAlias(), allFollowers);

                for(int limitCounter = 0; followersIndex < allFollowers.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                    responseFollowers.add(allFollowers.get(followersIndex));
                }

                hasMorePages = followersIndex < allFollowers.size();
            }
        }

        return new GetFollowersResponse(responseFollowers, hasMorePages);
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

    /**
     * Returns the list of dummy user data. This is written as a separate method to allow
     * mocking of the users.
     *
     * @return the users.
     */
    @Override
    public List<User> getDummyUsers() {
        return getFakeData().getFakeUsers();
    }

    public FollowResponse follow(FollowRequest request) {
        System.out.println(request.toString());
        System.out.println(request.getFollowerAlias() + " following " + request.getFolloweeAlias());

        setFollow(request.getFollowerAlias(), request.getFolloweeAlias(), "", "");
//        setFollow(request.);
        return new FollowResponse();
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        return new UnfollowResponse();
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        return new IsFollowerResponse(true);
    }







//
//    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
//            .credentialsProvider(ProfileCredentialsProvider.create())
//            .region(Region.US_WEST_2)
//            .build();
//    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
//            .dynamoDbClient(dynamoDbClient)
//            .build();

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    /**
     * Retrieve the follower name
     *
     * @param follower_handle
     * @param followee_handle
     * @return
     */
    private String getFollowerName(String follower_handle, String followee_handle) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        Follows follows = table.getItem(key);
        return follows == null ? "" : follows.getFollower_name();
    }

    /**
     * Retrieve the followee name
     *
     * @param follower_handle
     * @param followee_handle
     * @return
     */
    private String getFolloweeName(String follower_handle, String followee_handle) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        Follows follows = table.getItem(key);
        return follows == null ? "" : follows.getFollowee_name();
    }

    /**
     * Set follower name without setting followee name
     *
     * @param follower_handle
     * @param followee_handle
     * @param follower_name
     */
    private void setFollowerName(String follower_handle, String followee_handle, String follower_name) {
        setFollow(follower_handle, followee_handle, follower_name, "");
    }
    /**
     * set followee name without setting followername
     *
     * @param follower_handle
     * @param followee_handle
     * @param followee_name
     */
    private void setFolloweeName(String follower_handle, String followee_handle, String followee_name) {
        setFollow(follower_handle, followee_handle, "", followee_name);
    }
    /**
     * creates follow relationship
     *
     * @param follower_handle
     * @param followee_handle
     * @param followee_name
     */
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
    private List<Follows> getFollowees(String follower_handle, int pageSize, String lastFollowee) {
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
    private List<Follows> getFollowers(String followee_handle, int pageSize, String lastFollower) {
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
    private UserDao getUserDao(){
        if(this.userDao == null){
            this.userDao = new UserDao();
            return userDao;
        }
        return userDao;
    }
}
