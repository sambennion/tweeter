//package edu.byu.cs.tweeter.client.observer;
//
//import java.util.List;
//
//import edu.byu.cs.tweeter.model.domain.Status;
//
//public class GetStoryObserverConcrete implements GetStatusesObserver{
//    @Override
//    public void handleSuccess(List<Status> items, boolean hasMorePages) {
//        setLastItem((items.size() > 0) ? items.get(items.size() - 1) : null);
//        setHasMorePages(hasMorePages);
//
//        view.setLoading(false);
//        view.addItems(items);
//        setLoading(false);
//
//    }
//
//    @Override
//    public void handleFailure(String message) {
//
//    }
//
//    @Override
//    public void handleException(Exception exception) {
//
//    }
//
//    public void setLastItem(Status lastItem){
//        this.lastItem = lastItem;
//    }
//}
