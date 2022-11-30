package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDao;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterHandler extends Handler implements RequestHandler<RegisterRequest, RegisterResponse> {
    @Override
    public RegisterResponse handleRequest(RegisterRequest request, Context context) {
        UserService userService = new UserService(injector.getInstance(IFollowDAO.class), injector.getInstance(IStatusDAO.class), injector.getInstance(IUserDao.class));
        return userService.register(request);
    }
}
