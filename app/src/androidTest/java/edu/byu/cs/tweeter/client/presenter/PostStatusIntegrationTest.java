package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class PostStatusIntegrationTest {
    private ServerFacade serverFacade;

    private MainPresenter mainPresenterSpy;

    private MainPresenter.View mockView;

    private User user;

    private CountDownLatch countDownLatch;

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @BeforeEach
    public void setup(){
        resetCountDownLatch();

        serverFacade = Mockito.spy(new ServerFacade());

        mockView = Mockito.mock(MainPresenter.View.class);


//        Cache.getInstance().setCurrUserAuthToken(new AuthToken("12345678"));


        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));



        //real user I keep in dynamodb
//        user = new User("test", "test", "@test", "https://tweeter-images-bennion.s3.us-west-2.amazonaws.com/%40test");

//        Cache.getInstance().setCurrUser(user);

        Answer<Void> success = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
//                observer.handleFailure("my failure string");
                countDownLatch.countDown();
                return null;
            }
        };

        Answer<Void> fail = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
//                observer.handleFailure("my failure string");
                countDownLatch.countDown();
                return null;
            }
        };

        Mockito.doAnswer(success).when(mockView).displayInfoMessage("Successfully Posted!");

        Mockito.doAnswer(fail).when(mockView).displayErrorMessage(Mockito.any());

    }

    @Test
    public void testPost_postSuccess(){
        final String toPost = "Working test for testPost_postSuccess";


        //Login User
        try{
            LoginResponse loginResponse = serverFacade.login(new LoginRequest("@test", "test"), UserService.LOGIN_URL_PATH);
            System.out.println("Login response isSuccess: " + loginResponse.isSuccess() + "| message: " + loginResponse.getMessage());

            Cache.getInstance().setCurrUser(loginResponse.getUser());

            Cache.getInstance().setCurrUserAuthToken(loginResponse.getAuthToken());
        }
        catch (Exception exception){
            System.out.println("Exception in login request in testPost_postSuccess: " + exception.getMessage());
        }



        //Test post from presenter
        mainPresenterSpy.initiatePost(toPost, Cache.getInstance().getCurrUser());

        try{
            awaitCountDownLatch();
        }
        catch (InterruptedException interruptedException){
            System.out.println("InterruptedException: " + interruptedException.getMessage());
        }

        //Verify that Successfully Posted! was sent to the view
        Mockito.verify(mockView).displayInfoMessage("Successfully Posted!");


        //Retrieve the users story
        try{
            List<Status> statuses = new ArrayList<>();
            boolean hasMoreStatuses = true;
            Status lastStatus = null;
            while(hasMoreStatuses){
                StoryResponse storyResponse = serverFacade
                        .getStory(
                                new StoryRequest(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser().getAlias(), 25, lastStatus),
                                StatusService.STORY_URL_PATH);
                System.out.println("Story response isSuccess: " + storyResponse.isSuccess() + "| message: " + storyResponse.getMessage());

                System.out.println("Has more pages = " + storyResponse.getHasMorePages());
                hasMoreStatuses = storyResponse.getHasMorePages();
                lastStatus = storyResponse.getStatuses().get(storyResponse.getStatuses().size()-1);
                for(Status status : storyResponse.getStatuses()){
                    statuses.add(status);
                }
            }

            //Look for posted status in their story.
            boolean foundPost = false;

            Status retrievedPost = null;

            for(Status status : statuses){
                System.out.println("Checking post: " + status.getPost());
                if(status.getPost().equals(toPost)){
                    foundPost = true;
                    retrievedPost = status;
                    break;
                }
            }
            assert foundPost;
            assert retrievedPost != null;
            assert retrievedPost.getPost().equals(toPost);
            assert retrievedPost.getUser().equals(Cache.getInstance().getCurrUser());
        }
        catch (Exception exception){
            System.out.println("Exception in login request in testPost_postSuccess: " + exception.getMessage());
        }

    }


}
