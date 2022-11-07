package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.observer.LoginObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class LoginPresenter extends SignInPresenter implements LoginObserver {

    public LoginPresenter(SignInView view) {
        super(view);
    }

    @Override
    public String validate(UserInfo userInfo) {
        if (userInfo.alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (userInfo.alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (userInfo.alias.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    @Override
    public void signIn(UserInfo userInfo) {
        view.displayInfoMessage("Logging in ...");
        new UserService().login(userInfo.alias, userInfo.pword, this);
    }
}
