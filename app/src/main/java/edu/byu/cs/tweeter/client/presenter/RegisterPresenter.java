package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.observer.RegisterObserver;
import edu.byu.cs.tweeter.client.service.UserService;

public class RegisterPresenter extends SignInPresenter implements RegisterObserver {

    public RegisterPresenter(SignInPresenter.SignInView view) {
        super(view);
    }

    @Override
    public String validate(UserInfo userInfo) {
        if (userInfo.firstName.length() == 0) {
            return ("First Name cannot be empty.");
        }
        if (userInfo.lastName.length() == 0) {
            return ("Last Name cannot be empty.");
        }
        if (userInfo.alias.length() == 0) {
            return ("Alias cannot be empty.");
        }
        if (userInfo.alias.charAt(0) != '@') {
            return ("Alias must begin with @.");
        }
        if (userInfo.alias.length() < 2) {
            return ("Alias must contain 1 or more characters after the @.");
        }
        if (userInfo.pword.length() == 0) {
            return ("Password cannot be empty.");
        }

        if (userInfo.image.getDrawable() == null) {
            return ("Profile image must be uploaded.");
        }
        return null;
    }

    @Override
    public void signIn(UserInfo userInfo) {
        view.displayInfoMessage("Registering ...");
        new UserService().register(userInfo.firstName, userInfo.lastName, userInfo.alias, userInfo.pword, userInfo.image, this);
    }
}
