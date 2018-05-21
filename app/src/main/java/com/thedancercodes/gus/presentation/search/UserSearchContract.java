package com.thedancercodes.gus.presentation.search;

import com.thedancercodes.gus.data.remote.model.User;
import com.thedancercodes.gus.presentation.base.MvpPresenter;
import com.thedancercodes.gus.presentation.base.MvpView;

import java.util.List;

/**
 * Contracts between the View and the Presenter.
 *
 * The Activity should implement the View Contract.
 *
 * This is where the Android specific code will be located:
 * (things such as visibility changes or any UI changes will be located here).
 */
interface UserSearchContract {

    interface View extends MvpView {
        void showSearchResults(List<User> githubUserList);

        void showError(String message);

        void showLoading();

        void hideLoading();
    }

    interface Presenter extends MvpPresenter<View> {
        void search(String term);
    }
}
