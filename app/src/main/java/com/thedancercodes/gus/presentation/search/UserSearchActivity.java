package com.thedancercodes.gus.presentation.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.thedancercodes.gus.R;
import com.thedancercodes.gus.data.remote.model.User;
import com.thedancercodes.gus.injection.Injection;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserSearchActivity extends AppCompatActivity implements UserSearchContract.View {

    // This is the object that we will interact with in order to perform our network calls.
    private UserSearchContract.Presenter userSearchPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        /**
         * Create the presenter object.
         *
         * Provide it with the User repo defined in the Injection class.
         *
         * Pass the io() scheduler and the AndroidSchedulers.mainThread() scheduler
         * so that the RxJava subscriptions know which threads they should perform their work on.
          */

        userSearchPresenter = new UserSearchPresenter(Injection.provideUserRepo(), Schedulers.io(),
                AndroidSchedulers.mainThread());
        // Attach the view to the presenter, so that presenter can notify the view of any changes.
        userSearchPresenter.attachView(this);
    }

    /**
     * The presenter isn’t aware of the activity’s lifecycle.
     *
     * Because the presenter isn’t aware of the activity’s lifecycle, call detachView() to inform
     * the presenter that the view is no longer in existence.
     *
     * This will unregister any RxJava subscriptions and prevent memory leaks from occurring.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        userSearchPresenter.detachView();
    }

    @Override
    public void showSearchResults(List<User> githubUserList) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
