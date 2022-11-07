package edu.byu.cs.tweeter.client.model.service;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LoginHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.LogoutHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.client.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.observer.ILogoutObserver;
import edu.byu.cs.tweeter.client.observer.LoginObserver;
import edu.byu.cs.tweeter.client.observer.RegisterObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserService extends Service{

    public static final String LOGIN_URL_PATH = "/login";

    public void getUser(String userAlias, GetUserObserver observer) {
        runTask(new GetUserTask(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserHandler(observer)));
    }

    public void login(String username, String password, LoginObserver observer ){
        //Run LoginTask in the background to log the user in
        runTask(new LoginTask(username, password, new LoginHandler(observer)));
    }
    public void logout(AuthToken authToken, ILogoutObserver observer){
        runTask(new LogoutTask(authToken, new LogoutHandler(observer)));
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

}
