package edu.byu.cs.tweeter.model.net.response;

public class FollowingCountResponse extends Response{
    private int count;

    private FollowingCountResponse(String message) {
        super(false, message);
    }

    public FollowingCountResponse(int count){
        super(true, "Successfully retrieved following count");
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
