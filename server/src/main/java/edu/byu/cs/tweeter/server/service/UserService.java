package edu.byu.cs.tweeter.server.service;

import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.UserDao;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class UserService extends Service{

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        String hashedPassword = MD5Hashing.hashPassword(request.getPassword());

        Pair<User, AuthToken> userAuthTokenPair = getUserDao().login(request.getUsername(), hashedPassword);
        return new LoginResponse(userAuthTokenPair.getFirst(), userAuthTokenPair.getSecond());
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        String hashedPassword = MD5Hashing.hashPassword(request.getPassword());
        byte[] imageArray = Base64.getDecoder().decode(request.getImage());
        String imageURL = null;
        try{
            imageURL = getUserDao().uploadImage(imageArray, request.getUsername());
        }
        catch (Exception exception){
            System.out.println("Exception " + exception.getMessage());
            throw new RuntimeException("[Bad Request] Image couldn't upload to S3");
        }
        User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageURL);
        AuthToken authToken = getUserDao().register(user, hashedPassword);
        RegisterResponse response = new RegisterResponse(user, authToken);
        return response;
    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing a authtoken");
        }
        return new LogoutResponse();
//        return getUserDao().logout(request);
//        return new LogoutResponse();
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request){
        int followersCount = getUserDao().getFollowersCount(request);
        return new FollowersCountResponse(followersCount);
    }
    public FollowingCountResponse getFollowingCount(FollowingCountRequest request){
        int followingCount = getUserDao().getFollowingCount(request);
        return new FollowingCountResponse(followingCount);
    }

    public GetUserResponse getUser(GetUserRequest request){
        User user = getUserDao().getUserByAlias(request.getAlias());
        System.out.println("Got user " + user);
        return new GetUserResponse(user);
    }
//    /**
//     * Returns the dummy user to be returned by the login operation.
//     * This is written as a separate method to allow mocking of the dummy user.
//     *
//     * @return a dummy user.
//     */
//    User getDummyUser() {
//        return getFakeData().getFirstUser();
//    }
//
//    /**
//     * Returns the dummy auth token to be returned by the login operation.
//     * This is written as a separate method to allow mocking of the dummy auth token.
//     *
//     * @return a dummy auth token.
//     */
//    AuthToken getDummyAuthToken() {
//        return getFakeData().getAuthToken();
//    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
//    FakeData getFakeData() {
//        return FakeData.getInstance();
//    }

}
