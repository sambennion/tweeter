package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.observer.LogoutObserver;
import edu.byu.cs.tweeter.client.service.UserService;

public class MainPresenterUnitTest {

    private MainPresenter.View mockView;
    private UserService mockUserService;
    private Cache mockCache;

    MainPresenter mainPresenterSpy;
    @BeforeEach
    public void setup(){
        mockView = Mockito.mock(MainPresenter.View.class);
        mockUserService = Mockito.mock(UserService.class);
        mockCache = Mockito.mock(Cache.class);

//        MainPresenter presenter = new MainPresenter(mockView);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));

        Mockito.doReturn(mockUserService).when(mainPresenterSpy).getUserService();

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

    }
    @Test
    public void testPost_postFails(){

    }
}
