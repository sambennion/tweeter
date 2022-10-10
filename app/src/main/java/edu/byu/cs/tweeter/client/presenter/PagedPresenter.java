package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter<PagedPresenter.PagedView<T>> {
    protected int pageSize;
    protected User targetUser;
    protected AuthToken authToken;
    protected T lastItem;
    protected boolean hasMorePages;
    protected boolean isLoading;
    protected boolean isGettingUser;

    public interface PagedView<T> extends Presenter.View{
        void setLoading(boolean isLoading);

        void addItems(List<T> items);

        void navigateToUser(User user);

        void setHasMorePages(boolean hasMorePages);

        void displayErrorMessage(String message);

        void displayInfoMessage(String message);
    }

    public PagedPresenter(PagedView view, User user, AuthToken authToken){
        super(view);
        this.targetUser = user;
        this.authToken = authToken;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
//            view.setLoading(true);
            getItems(authToken, targetUser, pageSize, lastItem);
        }

    }
    protected void setLoading(boolean loading) {
        isLoading = loading;
//        view.setLoading(loading);
    }



    public void getUser(String alias){

    }

    protected abstract void getItems(AuthToken authToken, User targetUser, int pageSize, T lastItem);

    protected abstract String getDescription();

}
