package edu.byu.cs.tweeter.server.service.injectorModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDao;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDao;

public class ServiceModule extends AbstractModule{
    @Override
    protected void configure(){
        bind(IUserDao.class).to(UserDao.class);
        bind(IFollowDAO.class).to(FollowDAO.class);
        bind(IStatusDAO.class).to(StatusDAO.class);
    }
}
