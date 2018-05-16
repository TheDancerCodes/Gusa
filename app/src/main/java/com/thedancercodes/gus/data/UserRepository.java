package com.thedancercodes.gus.data;

import com.thedancercodes.gus.data.remote.model.User;

import java.util.List;

import rx.Observable;

/**
 * Created by TheDancerCodes on 09/05/2018.
 */

public interface UserRepository {

    Observable<List<User>> searchUsers(String searchTerm);
}
