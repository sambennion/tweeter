package edu.byu.cs.tweeter.server.dao.bean;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Follows {
    private String follower_handle;
    private String followee_handle;
    private String follower_name;
    private String followee_name;

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = FollowDAO.IndexName)
    public String getFollower_handle(){
        return this.follower_handle;
    }

    public void setFollower_handle(String followerHandle){
        this.follower_handle = followerHandle;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = FollowDAO.IndexName)
    public String getFollowee_handle() {
        return this.followee_handle;
    }

    public void setFollowee_handle(String followeeHandle){
        this.followee_handle = followeeHandle;
    }

    public String getFollower_name() {
        return follower_name;
    }

    public void setFollower_name(String follower_name) {
        this.follower_name = follower_name;
    }

    public String getFollowee_name() {
        return followee_name;
    }

    public void setFollowee_name(String followee_name) {
        this.followee_name = followee_name;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "follower_handle='" + follower_handle + '\'' +
                ", followee_handle='" + followee_handle + '\'' +
                ", follower_name='" + follower_name + '\'' +
                ", followee_name='" + followee_name +
                "'}";
    }

}
