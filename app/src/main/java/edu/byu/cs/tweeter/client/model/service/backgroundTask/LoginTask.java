package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    private static final String LOG_TAG = "LoginTask";

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";




    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }


    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {
        Pair<User, AuthToken> result = new Pair<>(null, null);
        try {
            LoginRequest request = new LoginRequest(username, password);
            LoginResponse response = getServerFacade().login(request, UserService.LOGIN_URL_PATH);

            if (response.isSuccess()) {
                result.setFirst(response.getUser());
                result.setSecond(response.getAuthToken());
//                Cache.getInstance().setCurrUser(response.getUser());
//                Cache.getInstance().setCurrUserAuthToken(response.getAuthToken());
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
        return result;
    }




}
