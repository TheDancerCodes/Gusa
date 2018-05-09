package com.thedancercodes.gus.data.remote;

import com.thedancercodes.gus.data.remote.model.User;
import com.thedancercodes.gus.data.remote.model.UsersList;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface GithubUserRestService {

    // This network call performs a search to get a list of users
    @GET("/search/users?per_page=2")
    Observable<UsersList> searchGithubUsers(@Query("q") String searchTerm);

    // This network call gets more details about a user.
    @GET
    Observable<User> getUser(@Path("username") String username);
}
