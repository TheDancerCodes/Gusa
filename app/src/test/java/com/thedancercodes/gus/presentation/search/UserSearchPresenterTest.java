package com.thedancercodes.gus.presentation.search;

import com.thedancercodes.gus.data.UserRepository;
import com.thedancercodes.gus.data.remote.model.User;
import com.thedancercodes.gus.data.remote.model.UsersList;
import com.thedancercodes.gus.presentation.base.BasePresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


public class UserSearchPresenterTest {

    private static final String USER_LOGIN_RIGGAROO = "riggaroo";
    private static final String USER_LOGIN2_REBECCA = "rebecca";

    // By creating a mock instance of UserRepository and the UserSearchContract.View we will ensure
    // that we are only testing the UserSearchPresenter.
    @Mock
    UserRepository userRepository;

    @Mock
    UserSearchContract.View view;

    UserSearchPresenter userSearchPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userSearchPresenter = new UserSearchPresenter(userRepository,
                Schedulers.immediate(), Schedulers.immediate());
        userSearchPresenter.attachView(view);
    }

    /**
     * Test that a valid search term has the correct callbacks
     */
    @Test
    public void search_ValidSearchTerm_ReturnsResults() {

        // Given - the user repository returns a set of users,
        UsersList usersList = getDummyUserList();
        when(userRepository.searchUsers(anyString()))
                .thenReturn(Observable.<List<User>>just(usersList.getItems()));

        // When - calling  search() on the presenter
        userSearchPresenter.search("riggaroo");

        // Then - the view methods showLoading() and showSearchResults() are called.
        // This test also asserts that the showError() method is never called.
        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view).showSearchResults(usersList.getItems());
        verify(view, never()).showError(anyString());
    }

    UsersList getDummyUserList() {
        List<User> githubUsers = new ArrayList<>();
        githubUsers.add(user1FullDetails());
        githubUsers.add(user2FullDetails());
        return new UsersList(githubUsers);
    }

    User user1FullDetails() {
        return new User(USER_LOGIN_RIGGAROO, "Rigs Franks", "avatar_url", "Bio1");
    }

    User user2FullDetails() {
        return new User(USER_LOGIN2_REBECCA, "Rebecca Franks", "avatar_url2", "Bio2");
    }

    /**
     * Test the negative scenario if the UserRepository throws an error.
     */
    @Test
    public void search_UserRepositoryError_ErrorMsg() {
        String errorMsg = "No Internet";

        // Given - the userRepository  returns an exception
        when(userRepository.searchUsers(anyString()))
                .thenReturn(Observable.error(new IOException(errorMsg)));

        // When - calling search()
        userSearchPresenter.search("thedancercodes");

        // Then - showError() should be called.
        verify(view).showLoading();
        verify(view).hideLoading();
        verify(view, never()).showSearchResults(anyList());
        verify(view).showError(errorMsg);
    }

    /**
     *  Assert that if the view is not attached, an exception will be thrown.
     */

    @Test(expected = BasePresenter.MvpViewNotAttachedException.class)
    public void search_NotAttached_ThrowsMvpException() {

        // Given - the view is not attached
        userSearchPresenter.detachView();

        // When - calling search()
        userSearchPresenter.search("test");

        // Then - an exception will be thrown.
        verify(view, never()).showLoading();
        verify(view, never()).showSearchResults(anyList());
    }



}