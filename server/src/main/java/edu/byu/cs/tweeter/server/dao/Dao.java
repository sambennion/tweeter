package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.util.FakeData;

public class Dao {
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
