package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.observer.SignInObserver;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class SignInPresenter extends Presenter<SignInPresenter.SignInView> implements SignInObserver {
    public SignInPresenter(SignInPresenter.SignInView view) {
        super(view);
    }

    public interface SignInView extends Presenter.View{
        void clearInfoMessage();

        void clearErrorMessage();

    }

    @Override
    public void handleFailure(String message) {
        view.clearInfoMessage();
        view.displayErrorMessage(message);
    }

    @Override
    public void handleException(Exception exception) {
        view.clearInfoMessage();
        view.displayErrorMessage("Sign in failed due to exception: " + exception.getMessage());
    }
    @Override
    public void signInSucceeded(User user, AuthToken authToken) {
        view.displayInfoMessage("Hello " + user.firstName);
        view.clearErrorMessage();
        view.navigateToUser(user);
    }
}
