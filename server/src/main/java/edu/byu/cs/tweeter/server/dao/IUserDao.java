package edu.byu.cs.tweeter.server.dao;

import java.io.IOException;

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
import edu.byu.cs.tweeter.util.Pair;

public interface IUserDao {
    Pair<User, AuthToken> login(String username, String password);

    AuthToken register(User user, String password);

    void logout(LogoutRequest request);

    int getFollowersCount(FollowersCountRequest request);

    int getFollowingCount(FollowingCountRequest request);

//    GetUserResponse getUser(GetUserRequest request);

    User getUserByAlias(String alias);

    void incrementFollowerCount(String alias);

    void incrementFollowingCount(String alias);

    void decrementFollowerCount(String alias);

    void decrementFollowingCount(String alias);

    String uploadImage(byte[] imageArray, String alias) throws IOException;
}
