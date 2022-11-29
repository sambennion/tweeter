package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDao;

public class Service {

    protected UserDao getUserDao(){
        return new UserDao();
    }

    protected FollowDAO getFollowDAO() {
        return new FollowDAO();
    }

    protected StatusDAO getStatusDao(){
        return new StatusDAO();
    }
}
