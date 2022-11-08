package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.client.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter<PagedPresenter.PagedView<T>> implements PagedObserver<T>, GetUserObserver {
    protected int pageSize = 10;
    protected User targetUser;
    protected AuthToken authToken;
    protected T lastItem;
    protected boolean hasMorePages = true;
    protected boolean isLoading = false;

    public PagedPresenter(User user, AuthToken authToken) {
        this.authToken = authToken;
        this.targetUser = user;
    }
//    protected boolean isGettingUser;

    public interface PagedView<T> extends View{
        void setLoading(boolean isLoading);

        void addItems(List<T> items);

        void setHasMorePages(boolean hasMorePages);


    }

    public PagedPresenter(PagedView view, User user, AuthToken authToken){
        super(view);
        this.targetUser = user;
        this.authToken = authToken;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);
            getItems(authToken, targetUser, pageSize, lastItem);
        }

    }
    protected void setHasMorePages(boolean hasMorePages){
        this.hasMorePages = hasMorePages;
        view.setHasMorePages(hasMorePages);
    }
    protected void setLoading(boolean loading) {
        isLoading = loading;
    }

    public T getLastItem(){
        return lastItem;
    }
    public void setLastItem(T lastItem){
        this.lastItem = lastItem;
    }

    public void getUser(String alias){
        view.displayInfoMessage("Getting user's profile...");
        new UserService().getUser(alias, this);
    }
    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
    }



    protected abstract void getItems(AuthToken authToken, User targetUser, int pageSize, T lastItem);

    protected abstract String getDescription();

    @Override
    public void handleFailure(String message) {
        String errorMessage = getDescription() + message;
        Log.e(getDescription(), errorMessage);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }
    @Override
    public void handleException(Exception exception) {
        String errorMessage = getDescription() + exception.getMessage();
        Log.e(getDescription(), errorMessage, exception);

        view.setLoading(false);
        view.displayErrorMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void handleSuccess(List<T> items, boolean hasMorePages) {
        setLastItem((items.size() > 0) ? items.get(items.size() - 1) : null);
        setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(items);
        setLoading(false);

    }

}
