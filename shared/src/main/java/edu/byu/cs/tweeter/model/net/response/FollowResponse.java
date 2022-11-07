package edu.byu.cs.tweeter.model.net.response;

public class FollowResponse extends Response{
    private FollowResponse(String message){
        super(false, message);
    }

    public FollowResponse(){
        super(true, "Successfully followed.");
    }
}
