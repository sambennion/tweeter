package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDao;

public class Service {
    protected IFollowDAO followDAO;
    protected IStatusDAO statusDAO;
    protected IUserDao userDao;

    public Service(IFollowDAO followDAO, IStatusDAO statusDAO, IUserDao userDao) {
        this.followDAO = followDAO;
        this.statusDAO = statusDAO;
        this.userDao = userDao;
    }
}
