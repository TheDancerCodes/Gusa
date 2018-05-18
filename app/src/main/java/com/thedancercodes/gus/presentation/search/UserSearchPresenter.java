package com.thedancercodes.gus.presentation.search;


import com.thedancercodes.gus.data.UserRepository;
import com.thedancercodes.gus.data.remote.model.User;
import com.thedancercodes.gus.presentation.base.BasePresenter;

import java.util.List;

import rx.Scheduler;
import rx.Subscriber;

/**
 * This is where a subscription to the UserRepository is created, which will call the Github API
 */
public class UserSearchPresenter extends BasePresenter<UserSearchContract.View>
        implements UserSearchContract.Presenter {

    private final Scheduler mainScheduler, ioScheduler;
    private UserRepository userRepository;

    UserSearchPresenter(UserRepository userRepository, Scheduler ioScheduler, Scheduler mainScheduler) {
        this.userRepository = userRepository;
        this.ioScheduler = ioScheduler;
        this.mainScheduler = mainScheduler;
    }

    @Override
    public void search(String term) {
        checkViewAttached();
        getView().showLoading();
        addSubscription(userRepository
                .searchUsers(term)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler).subscribe(new Subscriber<List<User>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().hideLoading();
                        //TODO: You probably don't want this error to show to users - Might want to show a friendlier message :)
                        getView().showError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<User> users) {
                        getView().hideLoading();
                        getView().showSearchResults(users);
                    }
                }));

    }
}
