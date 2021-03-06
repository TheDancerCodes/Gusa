package com.thedancercodes.gus.data;

import com.thedancercodes.gus.data.remote.GithubUserRestService;
import com.thedancercodes.gus.data.remote.model.User;
import com.thedancercodes.gus.data.remote.model.UsersList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by TheDancerCodes on 15/05/2018.
 */
public class UserRepositoryImplTest {

    private static final String USER_LOGIN_RIGAGROO = "riggaroo";
    private static final String USER_LOGIN_2_REBECCA = "rebecca";

    @Mock
    GithubUserRestService githubUserRestService;

    private UserRepository userRepository;

    // This method will run before any unit test.
    // It ensures that the Mock objects are setup before trying to use.
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Create an instance of the UserRepository  using the mocked out github service.
        userRepository = new UserRepositoryImpl(githubUserRestService);
    }

    /**
     * Test that the GithubUserRestService is called with the correct parameters &
     * that it returns the expected result.
     *
     * A TestSubscriber is subscribed to the search query observable.
     * Assertions are then done on the TestSubscriber to ensure it has the expected results.
     *
     * Naming structure for the tests:
     * [Name of method under test]_[Conditions of test case]_[Expected Result]
     */
    @Test
    public void searchUsers_200OkResponse_InvokeCorrectApiCalls() {

        // Given - The Github service returns certain users.
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(Observable.just(user1FullDetails()),
                        Observable.just(user2FullDetails()));

        // When - I search for users.
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RIGAGROO).subscribe(subscriber);

        // Then - The results should return and transform correctly.
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        List<List<User>> onNextEvents = subscriber.getOnNextEvents();
        List<User> users = onNextEvents.get(0);
        Assert.assertEquals(USER_LOGIN_RIGAGROO, users.get(0).getLogin());
        Assert.assertEquals(USER_LOGIN_2_REBECCA, users.get(1).getLogin());
        verify(githubUserRestService).searchGithubUsers(USER_LOGIN_RIGAGROO);
        verify(githubUserRestService).getUser(USER_LOGIN_RIGAGROO);
        verify(githubUserRestService).getUser(USER_LOGIN_2_REBECCA);
    }

    private UsersList githubUserList() {
        User user = new User(USER_LOGIN_RIGAGROO);
        User user2 = new User(USER_LOGIN_2_REBECCA);

        List<User> githubUsers = new ArrayList<>();
        githubUsers.add(user);
        githubUsers.add(user2);
        UsersList usersList = new UsersList(githubUsers);
        return usersList;
    }

    private User user1FullDetails() {
        User user = new User(USER_LOGIN_RIGAGROO, "Rigs Franks", "avatar_url", "Bio1");
        return user;
    }

    private User user2FullDetails() {
        User user = new User(USER_LOGIN_2_REBECCA, "Rebecca Franks", "avatar_url2", "Bio2");
        return user;
    }

    /**
     * Test if an IOException is thrown by the search service call
     * then the network call will be retried.
     *
     * In this test, we are asserting that the githubUserRestService was called twice and
     * the other network calls were called once.
     *
     * We also assert that there were no terminating errors on the subscriber.
     */
    @Test
    public void searchUsers_IOExceptionThenSuccess_SearchUsersRetired() {

        // Given
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(getIOExceptionError(), Observable.just(githubUserList()));
        when(githubUserRestService.getUser(anyString()))
                .thenReturn(Observable.just(user1FullDetails()),
                        Observable.just(user2FullDetails()));

        // When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RIGAGROO).subscribe(subscriber);

        // Then
        subscriber.awaitTerminalEvent();
        subscriber.assertNoErrors();

        verify(githubUserRestService, times(2)).searchGithubUsers(USER_LOGIN_RIGAGROO);

        verify(githubUserRestService).getUser(USER_LOGIN_RIGAGROO);
        verify(githubUserRestService).getUser(USER_LOGIN_2_REBECCA);
    }

//    @Test
//    public void searchUsers_GetUserIOExceptionThenSuccess_SearchUsersRetired() {
//
//        // Given
//        when(githubUserRestService.searchGithubUsers(anyString()))
//                .thenReturn(getIOExceptionError(), Observable.just(githubUserList()));
//        when(githubUserRestService.getUser(anyString()))
//                .thenReturn(Observable.just(user1FullDetails()),
//                        Observable.just(user2FullDetails()));
//
//        // When
//        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
//        userRepository.searchUsers(USER_LOGIN_RIGAGROO).subscribe(subscriber);
//
//        // Then
//        subscriber.awaitTerminalEvent();
//        subscriber.assertNoErrors();
//
//        verify(githubUserRestService, times(2)).searchGithubUsers(USER_LOGIN_RIGAGROO);
//
//        verify(githubUserRestService, times(2)).getUser(USER_LOGIN_RIGAGROO);
//        verify(githubUserRestService).getUser(USER_LOGIN_2_REBECCA);
//    }

    @Test
    public void searchUsers_OtherHttpError_SearchTerminatedWithError() {

        // Given
        when(githubUserRestService.searchGithubUsers(anyString()))
                .thenReturn(get403ForbiddenError());

        // When
        TestSubscriber<List<User>> subscriber = new TestSubscriber<>();
        userRepository.searchUsers(USER_LOGIN_RIGAGROO).subscribe(subscriber);

        // Then
        subscriber.awaitTerminalEvent();
        subscriber.assertError(HttpException.class);

        verify(githubUserRestService).searchGithubUsers(USER_LOGIN_RIGAGROO);

        verify(githubUserRestService, never()).getUser(USER_LOGIN_RIGAGROO);
        verify(githubUserRestService, never()).getUser(USER_LOGIN_2_REBECCA);
    }

    private Observable getIOExceptionError() {
        return Observable.error(new IOException());
    }


    private Observable<UsersList> get403ForbiddenError() {
        return Observable.error(new HttpException(
                Response.error(403,
                        ResponseBody.create(MediaType.parse("application/json"),
                                "Forbidden"))));
    }
}