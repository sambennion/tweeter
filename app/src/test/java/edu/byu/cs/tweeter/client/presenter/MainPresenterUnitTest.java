package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.observer.LogoutObserver;
import edu.byu.cs.tweeter.client.observer.PostStatusObserver;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;

public class MainPresenterUnitTest {

    private MainPresenter.View mockView;
    private UserService mockUserService;
    private Cache mockCache;
    private StatusService mockStatusService;
//    private

    private MainPresenter mainPresenterSpy;
    @BeforeEach
    public void setup(){
        mockView = Mockito.mock(MainPresenter.View.class);
        mockUserService = Mockito.mock(UserService.class);
        mockCache = Mockito.mock(Cache.class);

        mockStatusService = Mockito.mock(StatusService.class);
//        MainPresenter presenter = new MainPresenter(mockView);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));

        Mockito.doReturn(mockUserService).when(mainPresenterSpy).getUserService();

        Mockito.doReturn(mockStatusService).when(mainPresenterSpy).getStatusService();

        Cache.setInstance(mockCache);

//        Mockito.doReturn(new AuthToken("121341512")).when(mockCache).getCurrUserAuthToken();
    }

    @Test
    public void testLogout_logoutSucceeds(){
        Answer<Void> successAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                 LogoutObserver observer = invocation.getArgument(1, LogoutObserver.class);
                 observer.logoutSucceeded();
                 return null;
            }
        };


        Mockito.doAnswer(successAnswer).when(mockUserService).logout(Mockito.any(), Mockito.any());

        mainPresenterSpy.initiateLogout(Cache.getInstance().getCurrUserAuthToken());

        Mockito.verify(mockView).logoutUser();
        Mockito.verify(mockView).displayInfoMessage("Logging out...");
//        Mockito.verify(mockCache).clearCache();
    }
    @Test
    public void testLogout_logoutFails(){
        Answer<Void> failureAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                LogoutObserver observer = invocation.getArgument(1, LogoutObserver.class);
                observer.handleFailure("my failure string");
                return null;
            }
        };


        Mockito.doAnswer(failureAnswer).when(mockUserService).logout(Mockito.any(), Mockito.any());

        mainPresenterSpy.initiateLogout(Cache.getInstance().getCurrUserAuthToken());

//        Mockito.verify(mockView).displayInfoMessage("Logging Out...");
        Mockito.verify(mockView, Mockito.times(0)).logoutUser();
        Mockito.verify(mockView).displayErrorMessage("my failure string");
    }
    @Test
    public void testPost_postSucceeds(){
        Answer<Void> successAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assert invocation.getArgument(0) == Cache.getInstance().getCurrUserAuthToken();
                assert invocation.getArgument(1, Status.class).post.equals("This is a post");
                PostStatusObserver observer = invocation.getArgument(2, PostStatusObserver.class);
                assert observer != null;
                observer.handleSuccess();
                return null;
            }
        };
        Mockito.doAnswer(successAnswer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        mainPresenterSpy.initiatePost("This is a post", Cache.getInstance().getCurrUser());
        Mockito.verify(mockView).displayInfoMessage("Successfully Posted!");
    }
    @Test
    public void testPost_postFails(){
        Answer<Void> failureAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assert invocation.getArgument(0) == Cache.getInstance().getCurrUserAuthToken();
                assert invocation.getArgument(1, Status.class).post.equals("This is a post");
                PostStatusObserver observer = invocation.getArgument(2, PostStatusObserver.class);
                assert observer != null;
                observer.handleFailure("Failed to post status");
                return null;
            }
        };
        Mockito.doAnswer(failureAnswer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        mainPresenterSpy.initiatePost("This is a post", Cache.getInstance().getCurrUser());

        Mockito.verify(mockView, Mockito.times(0)).displayInfoMessage("Successfully Posted!");
        Mockito.verify(mockView).displayErrorMessage("Failed to post status");
    }
    @Test
    public void testPost_postException(){
        Answer<Void> exceptionAnswer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assert invocation.getArgument(0) == Cache.getInstance().getCurrUserAuthToken();
                assert invocation.getArgument(1, Status.class).post.equals("This is a post");
                PostStatusObserver observer = invocation.getArgument(2, PostStatusObserver.class);
                assert observer != null;
                observer.handleException(new Exception("this is an exception"));
                return null;
            }
        };
        Mockito.doAnswer(exceptionAnswer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

        mainPresenterSpy.initiatePost("This is a post", Cache.getInstance().getCurrUser());

        Mockito.verify(mockView, Mockito.times(0)).displayInfoMessage("Successfully Posted!");
        Mockito.verify(mockView, Mockito.times(0)).displayErrorMessage("Failed to post status");
        Mockito.verify(mockView).displayErrorMessage("Exception: this is an exception");
    }

}
