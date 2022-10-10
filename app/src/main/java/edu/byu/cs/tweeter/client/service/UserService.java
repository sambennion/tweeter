package edu.byu.cs.tweeter.client.service;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service{



    public interface LoginObserver{
        void loginSucceeded(User user, AuthToken authToken);
        void loginFailed(String message);
    }
    public interface RegisterObserver{
        void registerSucceeded(User user, AuthToken authToken);
        void registerFailed(String message);
    }
    public interface LogoutObserver{
        void logoutSucceeded();
        void logoutFailed(String message);
    }
    public interface GetUserObserver {
        void getUserSucceeded(User user);
        void getUserFailed(String message);
    }
    public void getUser(String userAlias, GetUserObserver observer) {
        runTask(new GetUserTask(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserHandler(observer)));
    }

    public void login(String username, String password, LoginObserver observer ){
        //Run LoginTask in the background to log the user in
        runTask(new LoginTask(username, password, new LoginHandler(observer)));
    }
    public void logout(AuthToken authToken, LogoutObserver observer){
        runTask(new LogoutTask(authToken, new LogoutHandler(observer)));
    }

    private class LoginHandler extends Handler {
        private LoginObserver observer;
        public LoginHandler(LoginObserver observer){
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LoginTask.SUCCESS_KEY);
            if (success) {
                User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

                // Cache user session information
                Cache.getInstance().setCurrUser(loggedInUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);
                observer.loginSucceeded(loggedInUser, authToken);

            } else if (msg.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LoginTask.MESSAGE_KEY);
                observer.loginFailed("Failed to login " + message);

            } else if (msg.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LoginTask.EXCEPTION_KEY);
                observer.loginFailed("Failed to login because of exception: " + ex.getMessage());
            }
        }
    }
    private class LogoutHandler extends Handler {
        private LogoutObserver observer;
        public LogoutHandler(LogoutObserver observer){
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LoginTask.SUCCESS_KEY);
            if (success) {
                // Cache user session information
                observer.logoutSucceeded();

            } else if (msg.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LoginTask.MESSAGE_KEY);
                observer.logoutFailed("Failed to logout " + message);

            } else if (msg.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LoginTask.EXCEPTION_KEY);
                observer.logoutFailed("Failed to login because of exception: " + ex.getMessage());
            }
        }
    }

    public void register(String firstName, String lastName, String alias, String password, ImageView image, RegisterObserver observer){
        // Convert image to byte array.
        Bitmap imageMap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageMap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);


        runTask(new RegisterTask(firstName, lastName, alias, password, imageBytesBase64, new RegisterHandler(observer)));
    }

    private class RegisterHandler extends Handler {
        private RegisterObserver observer;
        public RegisterHandler(RegisterObserver observer){
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(RegisterTask.SUCCESS_KEY);
            if (success) {
                User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

                Cache.getInstance().setCurrUser(registeredUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                observer.registerSucceeded(registeredUser, authToken);

            } else if (msg.getData().containsKey(RegisterTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(RegisterTask.MESSAGE_KEY);
                observer.registerFailed("Failed to register " + message);

            } else if (msg.getData().containsKey(RegisterTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(RegisterTask.EXCEPTION_KEY);
                observer.registerFailed("Failed to register due to exception " + ex.getMessage());
            }
        }
    }
    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends Handler {
        private GetUserObserver observer;
        public GetUserHandler(GetUserObserver observer){this.observer = observer;}
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);

                observer.getUserSucceeded(user);
            } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
                observer.getUserFailed("Failed to get user's profile: " + message);
            } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
                observer.getUserFailed("Failed to get user's profile because of exception: " + ex.getMessage());
            }
        }
    }

}
