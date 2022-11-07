package edu.byu.cs.tweeter.model.net.response;

public class PostStatusResponse extends Response{
    private PostStatusResponse(String messsage){
        super(false, messsage);
    }
    public PostStatusResponse(){
        super(true, "Successfully posted status.");
    }
}
