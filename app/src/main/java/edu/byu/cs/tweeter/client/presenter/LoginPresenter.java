package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter implements UserService.LoginObserver {

    private View view;

    public LoginPresenter(LoginPresenter.View view){
        this.view = view;
    }
    public void initiateLogin(String username, String password){
        String message = validateLogin(username, password);
        view.clearErrorMessage();
        if(message == null){
            view.displayInfoMessage("Logging in ...");
            new UserService().login(username, password, this);
        }
        else{
            view.displayErrorMessage(message);
        }
    }
    //Returns error message if error, otherwise null
    public String validateLogin(String username, String password) {
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }
    @Override
    public void loginSucceeded(User user, AuthToken authToken) {
        view.displayInfoMessage("Hello " + user.firstName);
        view.clearErrorMessage();
        view.navigateToUser(user);
    }

    @Override
    public void loginFailed(String message) {
        view.clearInfoMessage();
        view.displayErrorMessage(message);

    }


    public interface View{
        void displayInfoMessage(String message);
        void clearInfoMessage();

        void displayErrorMessage(String message);
        void clearErrorMessage();

        void navigateToUser(User user);

    }

}
