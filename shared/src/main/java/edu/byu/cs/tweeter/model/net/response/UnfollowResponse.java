package edu.byu.cs.tweeter.model.net.response;

public class UnfollowResponse extends Response{
    private UnfollowResponse(String message){
        super(false, message);
    }

    public UnfollowResponse(){
        super(true, "Successfully unfollowed.");
    }
}
