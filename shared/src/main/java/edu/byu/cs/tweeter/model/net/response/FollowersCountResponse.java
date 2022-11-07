package edu.byu.cs.tweeter.model.net.response;

public class FollowersCountResponse extends Response{
    private int count;


    private FollowersCountResponse(String message){
        super(false, message);
    }

    public FollowersCountResponse(int count){
        super(true, "Successfully got follower count");
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
