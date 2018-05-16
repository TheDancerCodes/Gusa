package com.thedancercodes.gus.presentation.base;


import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * BasePresenter provides functionality to check whether a view is attached to the presenter
 * and a way to manage RxJava subscriptions.
 *
 * @param <T>
 */
public class BasePresenter<T extends MvpView> implements MvpPresenter<T> {

    private T view;

    // This object holds a group of RxJava subscriptions.
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void attachView(T mvpView) {
        view = mvpView;
    }

    @Override
    public void detachView() {
        // unsubscribe from all subscriptions; prevent memory leaks and view crashes
        compositeSubscription.clear();
        view = null;
    }

    public T getView() {
        return view;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) {
            throw new MvpViewNotAttachedException();
        }
    }

    private boolean isViewAttached() {
        return view != null;
    }

    //  When a subscription is created in a presenter that subclasses this object
    // (CompositeSubscription), we will call addSubscription()
    protected void addSubscription(Subscription subscription) {
        this.compositeSubscription.add(subscription);
    }

    protected static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before"
                    + "requesting data to the Presenter");
        }
    }
}
