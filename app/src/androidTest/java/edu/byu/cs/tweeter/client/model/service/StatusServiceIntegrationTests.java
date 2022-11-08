package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.client.observer.ILogoutObserver;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServiceIntegrationTests {

    StatusService statusService;
    GetStatusesObserver getStoryObserver;
    AuthToken authToken;
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
        statusService = new StatusService();
        authToken = new AuthToken("12345");
//        getStoryObserver =  Mockito.mock(new StoryPresenter(FakeData.getInstance().getFirstUser(), authToken));
        getStoryObserver = Mockito.mock(GetStatusesObserver.class);
//        Mockito.doReturn("Success").when(getStoryObserver).handleSuccess(FakeData.getInstance().getFakeStatuses(),true);
        resetCountDownLatch();

        Answer<Void> success = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
//                observer.handleFailure("my failure string");
                countDownLatch.countDown();
                return null;
            }
        };

        Mockito.doAnswer(success).when(getStoryObserver).handleSuccess(Mockito.anyList(), Mockito.anyBoolean());



    }

    @Test
    public void testGetStory() throws InterruptedException {


        statusService.getStory(new AuthToken("12345"), FakeData.getInstance().getFirstUser(), 10, null, getStoryObserver);

        awaitCountDownLatch();

        Mockito.verify(getStoryObserver).handleSuccess(Mockito.anyList(), Mockito.anyBoolean());

    }

}
