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
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDao;
import edu.byu.cs.tweeter.server.dao.UserDao;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class UserService extends Service{

    public UserService(IFollowDAO followDAO, IStatusDAO statusDAO, IUserDao userDao) {
        super(followDAO, statusDAO, userDao);
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        String hashedPassword = MD5Hashing.hashPassword(request.getPassword());

        Pair<User, AuthToken> userAuthTokenPair = userDao.login(request.getUsername(), hashedPassword);
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
            imageURL = userDao.uploadImage(imageArray, request.getUsername());
        }
        catch (Exception exception){
            System.out.println("Exception " + exception.getMessage());
            throw new RuntimeException("[Bad Request] Image couldn't upload to S3");
        }
        User user = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageURL);
        AuthToken authToken = userDao.register(user, hashedPassword);
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
        int followersCount = userDao.getFollowersCount(request);
        return new FollowersCountResponse(followersCount);
    }
    public FollowingCountResponse getFollowingCount(FollowingCountRequest request){
        int followingCount = userDao.getFollowingCount(request);
        return new FollowingCountResponse(followingCount);
    }

    public GetUserResponse getUser(GetUserRequest request){
        User user = userDao.getUserByAlias(request.getAlias());
        System.out.println("Got user " + user);
        return new GetUserResponse(user);
    }


}
