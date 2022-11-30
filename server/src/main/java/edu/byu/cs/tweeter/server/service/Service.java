package edu.byu.cs.tweeter.server.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    protected boolean isExpiredAuthToken(String time){
        System.out.println("Time about to be checked is " + time);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date authTokenTime;
        try{
            authTokenTime = dateFormat.parse(time);
        }
        catch (Exception e){
            throw new RuntimeException("Error parsing date time in authtoken");
        }

        Date currentTime = date;

        if(authTokenTime.compareTo(currentTime) > 0){
            return true;
        }
        else{
            return false;
        }
    }
}
