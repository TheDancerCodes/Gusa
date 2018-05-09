package com.thedancercodes.gus.data;

import com.thedancercodes.gus.data.remote.GithubUserRestService;
import com.thedancercodes.gus.data.remote.model.User;

import java.io.IOException;
import java.util.List;

import rx.Observable;


/**
 * This is where we will combine the two network calls and transform the data into a view that
 * will be used in the front end.
 *
 * This is using RxJava to first get a list of users for the search term and
 * then for each user it does another network call to find out more of the user’s information.
 */
public class UserRepositoryImpl implements UserRepository {

    private GithubUserRestService mGithubUserRestService;

    public UserRepositoryImpl(GithubUserRestService githubUserRestService) {
        this.mGithubUserRestService = githubUserRestService;
    }


    // TODO: You might want to add a terminating condition; Only retrying a certain no. of times
    @Override
    public Observable<List<User>> searchUsers(final String searchTerm) {
        return Observable.defer(() -> mGithubUserRestService.searchGithubUsers(searchTerm)
                .concatMap(usersList -> Observable.from(usersList.getItems())
                .concatMap(user -> mGithubUserRestService.getUser(user.getLogin())).toList()))
                .retryWhen(observable -> observable.flatMap(o -> {
                    if (o instanceof IOException) {
                        return Observable.just(null);
                    }
                    return Observable.error(o);
                }));

    }

}
