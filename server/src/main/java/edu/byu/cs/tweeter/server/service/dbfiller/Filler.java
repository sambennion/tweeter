package edu.byu.cs.tweeter.server.service.dbfiller;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDao;
import edu.byu.cs.tweeter.server.dao.bean.UserBean;

public class Filler {


//need to delete guy57000 to guy57226

    // How many follower users to add
    // We recommend you test this with a smaller number first, to make sure it works for you
//    private final static int NUM_USERS = 10000;
    private final static int NUM_USERS = 4325;

    // The alias of the user to be followed by each user created
    // This example code does not add the target user, that user must be added separately.
    private final static String FOLLOW_TARGET = "@sbennion";

    public static void fillDatabase() {

        // Get instance of DAOs by way of the Abstract Factory Pattern
        UserDao userDAO = new UserDao();
        FollowDAO followDAO = new FollowDAO();

        List<String> followers = new ArrayList<>();
        List<UserBean> users = new ArrayList<>();

        // Iterate over the number of users you will create
        for (int i = 5676; i <= NUM_USERS+5675; i++) {

            String name = "Guy " + i;
            String alias = "guy" + i;

            // Note that in this example, a UserDTO only has a name and an alias.
            // The url for the profile image can be derived from the alias in this example
            UserBean user = new UserBean();
            user.setAlias(alias);
            user.setFirstName(name);
            user.setLastName(name);
            users.add(user);

            // Note that in this example, to represent a follows relationship, only the aliases
            // of the two users are needed
            followers.add(alias);
        }

//        // Call the DAOs for the database logic
//        if (users.size() > 0) {
//            userDAO.addUserBatch(users);
//        }
//        System.out.println(followers.size());
        if (followers.size() > 0) {
            followDAO.addFollowersBatch(followers, FOLLOW_TARGET);
//            userDAO.incrementFollowingCount(FOLLOW_TARGET);
        }
    }

    public static void main(String[] args) {
//        System.out.println("hello");

//        Filler.fillDatabase();
    }
}
