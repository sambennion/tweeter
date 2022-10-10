package edu.byu.cs.tweeter.client.presenter;

public abstract class Presenter<T extends Presenter.View>{
    protected T view;

    public Presenter(T view){
        this.view = view;
    }
    public interface View{
        void displayErrorMessage(String message);

        void displayInfoMessage(String message);

        void clearInfoMessage();
    }
}
