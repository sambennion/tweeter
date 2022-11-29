package edu.byu.cs.tweeter.server.dao.bean;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Authtoken {
    private String authtoken;
    private String timestamp;

    @DynamoDbPartitionKey
    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        return"Authtoken{authtoken = " + authtoken + '\'' +
                ", timestamp = " + timestamp + "}";
    }
}
