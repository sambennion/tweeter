package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends SignInPresenter implements UserService.RegisterObserver {
//    private View view;

    public RegisterPresenter(SignInPresenter.SignInView view){
        super(view);
//        this.view = view;
    }

    public void initiateRegister(String firstName, String lastName, String alias, String password, ImageView image){

        String message = validateRegistration(firstName, lastName, alias, password, image);
        view.clearErrorMessage();
        if(message == null){
            view.displayInfoMessage("Registering...");
            new UserService().register(firstName,lastName, alias, password, image, this);
        }
        else{
            view.displayErrorMessage(message);
        }

    }
    public String validateRegistration(String firstName, String lastName, String alias, String password, ImageView image) {
        if (firstName.length() == 0) {
            return ("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            return ("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            return ("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            return ("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            return ("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            return ("Password cannot be empty.");
        }

        if (image.getDrawable() == null) {
            return ("Profile image must be uploaded.");
        }
        return null;
    }
    @Override
    public void registerSucceeded(User user, AuthToken authToken) {
        view.displayInfoMessage("Hello " + user.firstName);
        view.clearErrorMessage();
        view.navigateToUser(user);
    }
    @Override
    public void registerFailed(String message) {
        view.clearInfoMessage();
        view.displayErrorMessage(message);
    }
}
