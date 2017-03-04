package com.moviesfeed;

import android.text.TextUtils;
import android.util.Log;

import com.moviesfeed.activities.FeedActivity;
import com.moviesfeed.api.Filters;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MoviesFeed;
import com.moviesfeed.presenters.FeedPresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static com.moviesfeed.presenters.FeedPresenter.NOT_FOUND;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Pedro on 2017-02-19.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, TextUtils.class})
public class FeedPresenterTest {

    public static final String MOVIE_TITLE = "Batman";
    public static final int FIRST_PAGE = 1;
    public static final String MOVIE_OVERVIEW = "The dark knight";
    public static final String MOVIE_POSTER_PATH = "poster path";
    public static final String MOVIE_BACKDROP_PATH = "backdrop path";
    public static final int MOVIE_VOTE_AVERAGE = 10;
    public static final int TOTAL_PAGES = 1;

    private FeedActivity activity;
    private FeedPresenter presenter;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(TextUtils.class);
        initPresenter();
    }

    private void initPresenter() {
        this.presenter = new FeedPresenter();
        this.presenter.onCreate(null);

        DaggerTestComponent.builder()
                .testAppModule(new TestAppModule()).build().inject(this.presenter);

        this.activity = mock(FeedActivity.class);
        this.presenter.takeView(activity);
    }

    @Test
    public void testSearchMovie() {

        final MoviesFeed moviesFeedTest = createMoviesFeed(Filters.SEARCH);

        when(this.presenter.api.getSearchMovie(anyString(), anyInt())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(moviesFeedTest);
            }
        });

        presenter.requestSearchMoviesFeed(MOVIE_TITLE);

        verify(this.presenter.api).getSearchMovie(MOVIE_TITLE, FIRST_PAGE);

        verifyActivityResultSuccess(moviesFeedTest);

        verify(this.activity, never()).requestMoviesFeedError(anyBoolean(), anyBoolean());
    }

    @Test
    public void testRequestMoviesFeed() {

        final MoviesFeed moviesFeedTest = createMoviesFeed(Filters.POPULARITY);

        when(this.presenter.api.getMoviesFilterBy(anyString(), anyInt())).then(new Answer<Observable>() {
            @Override
            public Observable answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(moviesFeedTest);
            }
        });

        presenter.requestMoviesFeed(Filters.POPULARITY);

        verify(this.presenter.api).getMoviesFilterBy(Filters.POPULARITY.toString(), FIRST_PAGE);

        verifyActivityResultSuccess(moviesFeedTest);

        verify(this.activity, never()).requestMoviesFeedError(anyBoolean(), anyBoolean());
    }

    @Test
    public void testRequestNextPage() {
        //to try the request next page firstly request any movies feed and then request the next page and verify the result page.

        final MoviesFeed moviesFeedTest = createMoviesFeed(Filters.POPULARITY);

        when(this.presenter.api.getMoviesFilterBy(anyString(), anyInt())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(moviesFeedTest);
            }
        });

        presenter.requestMoviesFeed(Filters.POPULARITY);

        verify(this.presenter.api).getMoviesFilterBy(Filters.POPULARITY.toString(), FIRST_PAGE);

        moviesFeedTest.setPage(moviesFeedTest.getPage() + 1);

        presenter.requestNextPage();

        verify(this.presenter.api).getMoviesFilterBy(Filters.POPULARITY.toString(), moviesFeedTest.getPage());

        ArgumentCaptor<MoviesFeed> argument = ArgumentCaptor.forClass(MoviesFeed.class);

        verify(this.activity, times(1)).requestMoviesFeedSuccess(argument.capture(), eq(false), anyBoolean(), anyInt(), anyBoolean());
        verify(this.activity, times(1)).requestMoviesFeedSuccess(argument.capture(), eq(true), anyBoolean(), anyInt(), anyBoolean());

        List<MoviesFeed> moviesFeedList = argument.getAllValues();

        Assert.assertEquals(moviesFeedList.get(0).getPage(), FIRST_PAGE);
        Assert.assertEquals(moviesFeedList.get(1).getPage(), moviesFeedTest.getPage());

        verify(this.activity, never()).requestMoviesFeedError(anyBoolean(), anyBoolean());

    }

    @Test
    public void testRefreshMoviesFeed() {
        final MoviesFeed moviesFeedTest = createMoviesFeed(Filters.COMEDY);

        when(this.presenter.api.getMovieByGenre(anyInt(), anyString(), anyInt())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(moviesFeedTest);
            }
        });

        presenter.requestMoviesFeed(Filters.COMEDY);

        presenter.refreshMoviesFeed();

        verify(this.presenter.api, times(2)).getMovieByGenre(eq(Integer.valueOf(moviesFeedTest.getFilter().toString())), anyString(), eq(moviesFeedTest.getPage()));

        ArgumentCaptor<MoviesFeed> argument = ArgumentCaptor.forClass(MoviesFeed.class);

        verify(this.activity, times(1)).requestMoviesFeedSuccess(argument.capture(), anyBoolean(), eq(false), anyInt(), anyBoolean());
        verify(this.activity, times(1)).requestMoviesFeedSuccess(argument.capture(), anyBoolean(), eq(true), anyInt(), anyBoolean());

        List<MoviesFeed> moviesFeedList = argument.getAllValues();

        compareMovieFeeds(moviesFeedList.get(0), moviesFeedList.get(1));

        verify(this.activity, never()).requestMoviesFeedError(anyBoolean(), anyBoolean());
    }

    @Test
    public void testAllMoviesDownloaded() {
        final MoviesFeed moviesFeedTest = createMoviesFeed(Filters.POPULARITY);

        when(this.presenter.api.getMoviesFilterBy(anyString(), anyInt())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(moviesFeedTest);
            }
        });

        presenter.requestMoviesFeed(Filters.POPULARITY);

        ArgumentCaptor<MoviesFeed> argument = ArgumentCaptor.forClass(MoviesFeed.class);

        verify(this.activity, times(1)).requestMoviesFeedSuccess(argument.capture(), anyBoolean(), anyBoolean(), anyInt(), eq(true));

        List<MoviesFeed> moviesFeedList = argument.getAllValues();

        Assert.assertEquals(moviesFeedList.get(0).getTotalPages(), TOTAL_PAGES);

        verify(this.activity, never()).requestMoviesFeedError(anyBoolean(), anyBoolean());
    }

    @Test
    public void testDownloadError() {
        when(this.presenter.api.getMoviesFilterBy(anyString(), anyInt())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(NOT_FOUND);
            }
        });

        presenter.requestMoviesFeed(Filters.POPULARITY);

        when(this.presenter.api.getMovieByGenre(anyInt(), anyString(), anyInt())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(NOT_FOUND);
            }
        });

        presenter.requestMoviesFeed(Filters.COMEDY);


        when(this.presenter.api.getSearchMovie(anyString(), anyInt())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return Observable.just(NOT_FOUND);
            }
        });

        presenter.requestSearchMoviesFeed(MOVIE_TITLE);

        verify(this.activity, times(3)).requestMoviesFeedError(anyBoolean(), anyBoolean());
    }


    private void verifyActivityResultSuccess(final MoviesFeed moviesFeedTest) {
        ArgumentCaptor<MoviesFeed> argument = ArgumentCaptor.forClass(MoviesFeed.class);
        verify(this.activity, atLeastOnce()).requestMoviesFeedSuccess(argument.capture(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean());

        MoviesFeed moviesFeed = argument.getValue();
        compareMovieFeeds(moviesFeed, moviesFeedTest);
    }

    private void compareMovieFeeds(MoviesFeed moviesFeedSource, MoviesFeed moviesFeedTarget) {
        Assert.assertEquals(moviesFeedSource.getFilter(), moviesFeedTarget.getFilter());
        Assert.assertEquals(moviesFeedSource.getMovies().size(), moviesFeedTarget.getMovies().size());
        Assert.assertEquals(moviesFeedSource.getId(), moviesFeedTarget.getId());
        Assert.assertEquals(moviesFeedSource.getMovies().get(0).getTitle(), moviesFeedTarget.getMovies().get(0).getTitle());
        Assert.assertEquals(moviesFeedSource.getPage(), moviesFeedTarget.getPage());
    }

    private MoviesFeed createMoviesFeed(Filters filter) {
        MoviesFeed mf = new MoviesFeed();
        mf.setFilterAndId(filter);

        List<Movie> listMovie = new ArrayList<>();
        Movie m = new Movie();
        m.setTitle(MOVIE_TITLE);
        m.setOverview(MOVIE_OVERVIEW);
        m.setPosterPath(MOVIE_POSTER_PATH);
        m.setBackdropPath(MOVIE_BACKDROP_PATH);
        m.setVoteAverage(MOVIE_VOTE_AVERAGE);
        listMovie.add(m);

        mf.setMovies(listMovie);
        mf.setPage(FIRST_PAGE);
        mf.setTotalPages(TOTAL_PAGES);
        return mf;
    }
}
