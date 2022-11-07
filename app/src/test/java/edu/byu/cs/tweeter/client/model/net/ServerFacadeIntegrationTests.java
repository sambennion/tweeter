package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class ServerFacadeIntegrationTests {
    ServerFacade serverFacade;
    @BeforeEach
    public void setup(){
        serverFacade = new ServerFacade();
    }

    @Test
    public void testRegister(){
        try{
            RegisterResponse response = serverFacade.register(new RegisterRequest("@allen", "password123"), UserService.REGISTER_URL_PATH);
            Assertions.assertNotNull(response);
            Assertions.assertEquals(response.getUser().getAlias(), "@allen");

            Assertions.assertNotNull(response.getAuthToken());
        }
        catch (Exception e){
            Assertions.assertNull(e);
        }
    }

    @Test
    public void testGetFollowers(){
        try{
            GetFollowersResponse response = serverFacade.getFollowers(new GetFollowersRequest(new AuthToken("12345"), "@allen", 10, null), FollowService.GET_FOLLOWERS_URL_PATH);
            Assertions.assertNotNull(response);

            Assertions.assertNotNull(response.getFollowers());

            Assertions.assertTrue(response.getFollowers().size() > 0);


        }
        catch (Exception e){
            Assertions.assertNull(e);
        }
    }

    @Test
    public void testGetFollowersCount(){
        try {
            FollowersCountResponse response = serverFacade.getFollowersCount(new FollowersCountRequest(new AuthToken("12345"), "@allen"), FollowService.FOLLOWERS_COUNT_URL_PATH);

            Assertions.assertNotNull(response.getMessage());
            Assertions.assertNotNull(response.getCount());

            Assertions.assertTrue(response.getCount() > 0);

            Assertions.assertEquals(response.getMessage(), "Successfully got follower count");
        }
        catch (Exception e){
            Assertions.assertNull(e);
        }
    }

    @Test
    public void testGetFollowingCount(){
        try {
            FollowingCountResponse response = serverFacade.getFollowingCount(new FollowingCountRequest(new AuthToken("12345"), "@allen"), FollowService.FOLLOWING_COUNT_URL_PATH);

            Assertions.assertNotNull(response.getMessage());
            Assertions.assertNotNull(response.getCount());

            Assertions.assertTrue(response.getCount() > 0);

            Assertions.assertEquals(response.getMessage(), "Successfully retrieved following count");
        }
        catch (Exception e){
            Assertions.assertNull(e);
        }
    }
}
