package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response{
    private boolean isFollower;

    private IsFollowerResponse(String message){
        super(false, message);
    }

    public IsFollowerResponse(boolean isFollower) {
        super(true, "Successfully checked if isFollower");
        this.isFollower = isFollower;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }





}
