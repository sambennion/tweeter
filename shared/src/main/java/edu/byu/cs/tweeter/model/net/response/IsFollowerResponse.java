package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response{
    private boolean follower;

    private IsFollowerResponse(String message){
        super(false, message);
    }

    public IsFollowerResponse(boolean isFollower) {
        super(true, "Successfully checked if isFollower");
        this.follower = isFollower;
        System.out.println("IsFollower = " + this.follower);
    }

    public boolean isFollower() {
        return follower;
    }

    public void setFollower(boolean follower) {
        this.follower = follower;
    }





}
