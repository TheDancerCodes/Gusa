package com.thedancercodes.gus.presentation.base;

/**
 * A presenter which communicates with the repository and conveys information to the view.
 * @param <V> - MvpView
 */
public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
