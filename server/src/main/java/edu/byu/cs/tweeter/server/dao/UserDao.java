package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.server.dao.bean.Authtoken;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class UserDao extends Dao implements IUserDao {
    private static String S3BUCKET_KEY = "tweeter-images-bennion";
    public static final String UserIndexName = "alias";

    private static final String AuthtokenIndexName = "authtoken";


    @Override
    public Pair<User, AuthToken> login(String username, String password) {
        UserBean userBean = getUserBean(username);
        System.out.println("Hashed Pass = " + password + " stored pass = " + userBean.getPassword());
        if(password.equals(userBean.getPassword())){
            AuthToken authToken = new AuthToken(UUID.randomUUID().toString());
            return new Pair<>(convertUserBeanToUser(userBean), authToken);

        }
        else{
            throw new RuntimeException("Wrong password");
        }
    }



    private User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    @Override
    public AuthToken register(User user, String password) {

        AuthToken authToken = new AuthToken(UUID.randomUUID().toString());
        insertAuthtoken(authToken);
        setRegister(user, password);
        return authToken;
    }

    @Override
    public void logout(LogoutRequest request) {
        deleteAuthtoken(request.getAuthToken());
    }

    @Override
    public int getFollowersCount(FollowersCountRequest request) {
        UserBean userBean = getUserBean(request.getTargetUserAlias());
        return userBean.getFollowerCount();
    }

    @Override
    public int getFollowingCount(FollowingCountRequest request) {
        UserBean userBean = getUserBean(request.getTargetUserAlias());
        return userBean.getFollowingCount();
    }

    @Override
    public User getUserByAlias(String alias) {
        UserBean userBean = getUserBean(alias);
        if(userBean == null){
            System.out.println("User does not exist in db");
            throw new RuntimeException("User does not exist in db");
        }
        else{
            return new User(userBean.getFirstName(), userBean.getLastName(), userBean.getAlias(), userBean.getImageUrl());
        }
    }

    @Override
    public void incrementFollowerCount(String alias){
        DynamoDbTable<UserBean> table = getUserTable();
        UserBean userBean = getUserBean(alias);
        if(userBean == null){
            System.out.println("userBean == null");
            throw new RuntimeException("Error getting userBean in incrementFollowerCount for alias " + alias);
        }
        else{
            userBean.setFollowerCount(userBean.getFollowerCount()+1);
            table.updateItem(userBean);
        }
    }

    @Override
    public void incrementFollowingCount(String alias){
        DynamoDbTable<UserBean> table = getUserTable();
        UserBean userBean = getUserBean(alias);
        if(userBean == null){
            System.out.println("userBean == null");
            throw new RuntimeException("Error getting userBean in incrementFollowingCount for alias " + alias);
        }
        else{
            userBean.setFollowingCount(userBean.getFollowingCount()+1);
            table.updateItem(userBean);
        }
    }

    @Override
    public void decrementFollowerCount(String alias){
        DynamoDbTable<UserBean> table = getUserTable();
        UserBean userBean = getUserBean(alias);
        if(userBean == null){
            System.out.println("userBean == null");
            throw new RuntimeException("Error getting userBean in decrementFollowerCount for alias " + alias);
        }
        else{
            userBean.setFollowerCount(userBean.getFollowerCount()-1);
            table.updateItem(userBean);
        }
    }

    @Override
    public void decrementFollowingCount(String alias){
        DynamoDbTable<UserBean> table = getUserTable();
        UserBean userBean = getUserBean(alias);
        if(userBean == null){
            System.out.println("userBean == null");
            throw new RuntimeException("Error getting userBean in decrementFollowingCount for alias " + alias);
        }
        else{
            userBean.setFollowingCount(userBean.getFollowingCount()-1);
            table.updateItem(userBean);
        }
    }

    private void setRegister(User user, String password){
        DynamoDbTable<UserBean> table = getUserTable();
        Key key = buildKey(user.getAlias());
        //check if user is already registered
        UserBean userBean = table.getItem(key);
        if(userBean != null){
            System.out.println("User already registered");
            throw new RuntimeException("User already registered");
        }
        else{
            userBean = new UserBean();
            userBean.setAlias(user.getAlias());
            userBean.setFirstName(user.getFirstName());
            userBean.setLastName(user.getLastName());
            userBean.setImageUrl(user.getImageUrl());
            userBean.setPassword(password);
            userBean.setFollowerCount(0);
            userBean.setFollowingCount(0);
            table.putItem(userBean);
        }
    }
    private void insertAuthtoken(AuthToken authToken){
        DynamoDbTable<Authtoken> table = getAuthtokenTable();
        Key key = buildKey(authToken.getToken());
        Authtoken token = table.getItem(key);
        if(token != null){
            System.out.println("Token already inserted");
        }
        else{
            token = new Authtoken();
            token.setAuthtoken(authToken.getToken());
            token.setTimestamp(authToken.getDatetime());
            table.putItem(token);
        }

    }
    private void deleteAuthtoken(AuthToken authtoken){
        DynamoDbTable<Authtoken> table =  getAuthtokenTable();
        Key key = buildKey(authtoken.getToken());
        table.deleteItem(key);
    }

    @Override
    public String uploadImage(byte[] imageArray, String alias) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(imageArray);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/jpeg");
        objectMetadata.setContentLength(imageArray.length);
        s3.putObject(new PutObjectRequest(S3BUCKET_KEY, alias, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));

        return s3.getUrl(S3BUCKET_KEY, alias).toString();
    }

    private DynamoDbTable<UserBean> getUserTable(){
        return enhancedClient.table(UserTableName, TableSchema.fromBean(UserBean.class));
    }
    private DynamoDbTable<Authtoken> getAuthtokenTable(){
        return enhancedClient.table(AuthtokenTableName, TableSchema.fromBean(Authtoken.class));
    }




    private UserBean getUserBean(String alias){
        DynamoDbTable<UserBean> table = getUserTable();
        Key key = buildKey(alias);
        UserBean userBean = table.getItem(key);
        if(userBean == null){
            System.out.println("Couldn't get user");
            throw new RuntimeException("Couldn't get user");
        }
        return userBean;
    }

    private User convertUserBeanToUser(UserBean userBean){
        return new User(userBean.getFirstName(), userBean.getLastName(), userBean.getAlias(), userBean.getImageUrl());
    }

    @Override
    public void addUserBatch(List<UserBean> users) {
        List<UserBean> batchToWrite = new ArrayList<>();
        for (UserBean u : users) {
            UserBean dto = u;
            batchToWrite.add(dto);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserDTOs(batchToWrite);
        }
    }
    private void writeChunkOfUserDTOs(List<UserBean> userDTOs) {
        if(userDTOs.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<UserBean> table = enhancedClient.table(UserTableName, TableSchema.fromBean(UserBean.class));
        WriteBatch.Builder<UserBean> writeBuilder = WriteBatch.builder(UserBean.class).mappedTableResource(table);
        for (UserBean item : userDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
