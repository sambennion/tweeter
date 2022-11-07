package edu.byu.cs.tweeter.client.presenter;


import android.widget.ImageView;

import edu.byu.cs.tweeter.client.observer.SignInObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class SignInPresenter extends Presenter<SignInPresenter.SignInView> implements SignInObserver {
    public SignInPresenter(SignInView view) {
        super(view);
    }

    //A class so we can have template methods for validate and initiateSignIn.
    public static class UserInfo {
        public String alias;
        public String pword;
        public String firstName;
        public String lastName;
        public ImageView image;

        public UserInfo(String alias, String password) {
            this.alias = alias;
            this.pword = password;
        }

        public UserInfo(String fname, String lname, String uname, String pwrd, ImageView imageToUpload) {
            this.firstName = fname;
            this.lastName = lname;
            this.alias = uname;
            this.pword = pwrd;
            this.image = imageToUpload;
        }
    }

    public interface SignInView extends View {
        void clearInfoMessage();

        void clearErrorMessage();

    }

    public void initiateSignIn(UserInfo userInfo) {
        String message = validate(userInfo);
        view.clearErrorMessage();
        if (message == null) {
            signIn(userInfo);
        } else {
            view.displayErrorMessage(message);
        }
    }

    public abstract String validate(UserInfo userInfo);

    public abstract void signIn(UserInfo userInfo);

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
        view.displayInfoMessage("Hello " + user.getFirstName());
        view.clearErrorMessage();
        view.navigateToUser(user);
    }
}
