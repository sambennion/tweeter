package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class Dao {
    protected static final String StoryTableName = "story";
    protected static final String FeedTableName = "feed";
    protected static final String FollowsTableName = "follows";
    protected static final String UserTableName = "user";
    protected static final String AuthtokenTableName = "authtoken";


    protected static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();
    protected static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    protected AmazonS3 s3 = AmazonS3ClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();

    FakeData getFakeData() {
        return FakeData.getInstance();
    }

    public Key buildKey(String partitionValue){
        return Key.builder()
                .partitionValue(partitionValue)
                .build();
    }


}
