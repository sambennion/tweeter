package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.byu.cs.tweeter.client.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.client.presenter.PagedStatusPresenter;
import edu.byu.cs.tweeter.client.presenter.Presenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServiceIntegrationTests {

    StatusService statusService;
    GetStatusesObserver getStoryObserver;
    AuthToken authToken;

    @BeforeEach
    public void setup(){
        statusService = new StatusService();
        authToken = new AuthToken("12345");
        getStoryObserver =  Mockito.spy(new StoryPresenter(null, FakeData.getInstance().getFirstUser(), authToken));


//        Mockito.doReturn(true).when(getStoryObserver).handleSuccess(Mockito.anyList(), Mockito.anyBoolean());
//
//        Mockito.doReturn(false).when(getStoryObserver).handleException();




    }

    @Test
    public void testGetStory(){
        statusService.getStory(new AuthToken("12345"), FakeData.getInstance().getFirstUser(), 10, null, getStoryObserver);

        Mockito.verify(getStoryObserver).handleSuccess(Mockito.anyList(), Mockito.anyBoolean());
    }

}
