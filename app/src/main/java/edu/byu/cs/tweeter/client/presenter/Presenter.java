package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class Presenter<T extends Presenter.View> {
    protected T view;

    public Presenter(T view){
        this.view = view;
    }

    //This is for testing.
    public Presenter(){}


    public interface View{
        void displayErrorMessage(String message);

        void displayInfoMessage(String message);

        void navigateToUser(User user);

    }

}
