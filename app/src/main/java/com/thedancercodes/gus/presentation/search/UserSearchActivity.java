package com.thedancercodes.gus.presentation.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thedancercodes.gus.R;
import com.thedancercodes.gus.data.remote.model.User;
import com.thedancercodes.gus.injection.Injection;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserSearchActivity extends AppCompatActivity implements UserSearchContract.View {

    // This is the object that we will interact with in order to perform our network calls.
    private UserSearchContract.Presenter userSearchPresenter;

    private UsersAdapter usersAdapter;
    private SearchView searchView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewUsers;
    private TextView textViewErrorMessage;

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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress_bar);

        textViewErrorMessage = findViewById(R.id.text_view_error_msg);

        recyclerViewUsers = findViewById(R.id.recycler_view_users);

        usersAdapter = new UsersAdapter(null, this);

        recyclerViewUsers.setAdapter(usersAdapter);

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

    /**
     * Hook the SearchView up into our activity to make it trigger the presenters search() method.
     *
     *  Inflate the correct menu, find the search view and set a query text listener.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_user_search, menu);

        final MenuItem searchActionMenuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) searchActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                userSearchPresenter.search(query);
                toolbar.setTitle(query);
                searchActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchActionMenuItem.expandActionView();
        return true;
    }

    // Callbacks that the presenter calls when the items have finished loading.
    // We are toggling visibility of views here & setting the userAdapter to the new items
    // the service returned.
    @Override
    public void showSearchResults(List<User> githubUserList) {
        recyclerViewUsers.setVisibility(View.VISIBLE);
        textViewErrorMessage.setVisibility(View.GONE);
        usersAdapter.setItems(githubUserList);
    }

    @Override
    public void showError(String message) {
        textViewErrorMessage.setVisibility(View.VISIBLE);
        recyclerViewUsers.setVisibility(View.GONE);
        textViewErrorMessage.setText(message);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewUsers.setVisibility(View.GONE);
        textViewErrorMessage.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerViewUsers.setVisibility(View.VISIBLE);
        textViewErrorMessage.setVisibility(View.GONE);
    }
}
