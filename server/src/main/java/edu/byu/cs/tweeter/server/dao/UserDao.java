package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.bean.Authtoken;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class UserDao extends Dao implements IUserDao {
    private static String S3BUCKET_KEY = "tweeter-images-bennion";
    private static final String UserTableName = "user";
    public static final String UserIndexName = "alias";
    private static final String AuthtokenTableName = "authtoken";
    private static final String AuthtokenIndexName = "authtoken";



    @Override
    public LoginResponse login(LoginRequest request) {
        User user = getDummyUser();
        AuthToken authToken = new AuthToken(UUID.randomUUID().toString());
        return new LoginResponse(user, authToken);
    }



    private User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    private AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    @Override
    public AuthToken register(User user, String password) {

        AuthToken authToken = new AuthToken(UUID.randomUUID().toString());
        insertAuthtoken(authToken);
        setRegister(user, password);
        return authToken;
//        return new RegisterResponse(user, authToken);
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse();
    }

    @Override
    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        return new FollowersCountResponse(20);
    }

    @Override
    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        return new FollowingCountResponse(20);
    }

    @Override
    public GetUserResponse getUser(GetUserRequest request) {
        return new GetUserResponse(getFakeData().findUserByAlias(request.getAlias()));
    }

    @Override
    public User getUserByAlias(String alias) {
        return getFakeData().findUserByAlias(alias);
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
        DynamoDbTable<Authtoken> table = enhancedClient.table(AuthtokenTableName, TableSchema.fromBean(Authtoken.class));
        Key key = buildKey(authToken.getToken());
//        Key key = Key.builder()
//                .partitionValue(authToken.getToken())
//                .build();
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

    public String uploadImage(byte[] imageArray, String alias) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(imageArray);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/jpeg");
        objectMetadata.setContentLength(imageArray.length);
        s3.putObject(new PutObjectRequest(S3BUCKET_KEY, alias, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));

        return s3.getUrl(S3BUCKET_KEY, alias).toString();
    }

    public DynamoDbTable<UserBean> getUserTable(){
        return enhancedClient.table(UserTableName, TableSchema.fromBean(UserBean.class));
    }

    public Key buildKey(String partitionValue){
        return Key.builder()
                .partitionValue(partitionValue)
                .build();
    }

}
