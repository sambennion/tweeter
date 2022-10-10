package edu.byu.cs.tweeter.client.presenter;

public abstract class SignInPresenter extends Presenter<SignInPresenter.SignInView> {
    public SignInPresenter(SignInPresenter.SignInView view) {
        super(view);
    }

    public interface SignInView extends Presenter.View{
        void clearInfoMessage();

        void clearErrorMessage();

    }


}
