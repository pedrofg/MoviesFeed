package com.moviesfeed;

import android.text.TextUtils;
import android.util.Log;

import com.moviesfeed.activities.MovieDetailActivity;
import com.moviesfeed.models.Cast;
import com.moviesfeed.models.Credits;
import com.moviesfeed.models.Crew;
import com.moviesfeed.models.Genre;
import com.moviesfeed.models.Movie;
import com.moviesfeed.models.MovieBackdrop;
import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.models.MovieImages;
import com.moviesfeed.models.MoviePoster;
import com.moviesfeed.models.MovieReviews;
import com.moviesfeed.models.MovieVideos;
import com.moviesfeed.models.Review;
import com.moviesfeed.models.SimilarMovies;
import com.moviesfeed.models.Video;
import com.moviesfeed.presenters.MovieDetailPresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static com.moviesfeed.presenters.MovieDetailPresenter.NOT_FOUND;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Pedro on 2017-02-26.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, TextUtils.class})
public class MovieDetailPresenterTest {


    public static final int MOVIE_ID = 836401;
    public static final String OVERVIEW_TEST = "Overview Test";
    public static final int VOTE_AVERAGE = 2;
    public static final int BUDGET = 200;
    public static final String HOMEPAGE_TEST = "Homepage Test";
    public static final String IMDB_ID_TEST = "imdbId Test";
    public static final String POSTER_PATH_TEST = "Poster Path Test";
    public static final int REVENUE = 9998;
    public static final String TITLE_TEST = "Title Test";
    public static final int RUNTIME = 6789;
    public static final String CREW_JOB = "Director";
    public static final String CREW_NAME = "NameCrew Test";
    public static final String CAST_CHARACTER = "CharacterName test";
    public static final String CAST_NAME = "NameCrew Test";
    public static final int CREW_ID = 1;


    private MovieDetailActivity activity;
    private MovieDetailPresenter presenter;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);
        PowerMockito.mockStatic(TextUtils.class);
        initPresenter();
    }

    private void initPresenter() {
        this.presenter = new MovieDetailPresenter();
        this.presenter.onCreate(null);

        DaggerTestComponent.builder()
                .testAppModule(new TestAppModule()).build().inject(this.presenter);

        this.activity = mock(MovieDetailActivity.class);
        this.presenter.takeView(activity);
    }

    @Test
    public void testRequestMovieDetail() {

        final MovieDetail movieDetail = createMovieDetail();

        when(this.presenter.api.getMovieDetail(anyInt())).then(invocation -> Observable.just(movieDetail));

        presenter.requestMovieDetail(MOVIE_ID);

        verify(this.presenter.api).getMovieDetail(MOVIE_ID);

        verifyActivityResultSuccess(movieDetail);

        verify(this.activity, never()).requestMovieDetailError(anyBoolean());
    }

    @Test
    public void testDownloadError() {
        when(this.presenter.api.getMovieDetail(anyInt())).then(invocation -> Observable.just(NOT_FOUND));

        presenter.requestMovieDetail(MOVIE_ID);

        verify(this.presenter.api).getMovieDetail(MOVIE_ID);

        verify(this.activity, times(1)).requestMovieDetailError(anyBoolean());
    }


    private void verifyActivityResultSuccess(final MovieDetail movieDetailTest) {
        ArgumentCaptor<MovieDetail> argument = ArgumentCaptor.forClass(MovieDetail.class);
        verify(this.activity, atLeastOnce()).requestMovieDetailSuccess(argument.capture());

        MovieDetail movieDetail = argument.getValue();
        compareMoviesDetail(movieDetail, movieDetailTest);
    }

    private void compareMoviesDetail(MovieDetail moviesDetailSource, MovieDetail moviesDetailTarget) {
        Assert.assertEquals(moviesDetailSource.getId(), moviesDetailTarget.getId());
        Assert.assertEquals(moviesDetailSource.getCredits().getCast().size(), moviesDetailTarget.getCredits().getCast().size());
        Assert.assertEquals(moviesDetailSource.getCredits().getCrew().size(), moviesDetailTarget.getCredits().getCrew().size());
        Assert.assertEquals(moviesDetailSource.getGenres().size(), moviesDetailTarget.getGenres().size());
        Assert.assertEquals(moviesDetailSource.getImages().getBackdrops().size(), moviesDetailTarget.getImages().getBackdrops().size());
        Assert.assertEquals(moviesDetailSource.getImages().getPosters().size(), moviesDetailTarget.getImages().getPosters().size());
    }


    private MovieDetail createMovieDetail() {
        MovieDetail movieDetail = new MovieDetail();
        movieDetail.setOverview(OVERVIEW_TEST);
        movieDetail.setVoteAverage(VOTE_AVERAGE);
        movieDetail.setBudget(BUDGET);
        movieDetail.setHomepage(HOMEPAGE_TEST);
        movieDetail.setId((long) MOVIE_ID);
        movieDetail.setImdbId(IMDB_ID_TEST);
        movieDetail.setPosterPath(POSTER_PATH_TEST);
        movieDetail.setRevenue((long) REVENUE);
        movieDetail.setTitle(TITLE_TEST);
        movieDetail.setRuntime(RUNTIME);
        movieDetail.setGenres(new ArrayList<>());

        Credits credits = new Credits();

        Crew crew = new Crew();
        crew.setId(CREW_ID);
        crew.setJob(CREW_JOB);
        crew.setName(CREW_NAME);
        List<Crew> crewList = new ArrayList<>();
        crewList.add(crew);
        credits.setCrew(crewList);

        Cast cast = new Cast();
        cast.setId(1);
        cast.setCharacter(CAST_CHARACTER);
        cast.setName(CAST_NAME);
        List<Cast> castList = new ArrayList<>();
        castList.add(cast);
        credits.setCast(castList);

        movieDetail.setCredits(credits);


        MovieImages movieImages = new MovieImages();
        movieImages.setBackdrops(new ArrayList<>());
        movieImages.setPosters(new ArrayList<>());

        movieDetail.setImages(movieImages);


        MovieVideos movieVideos = new MovieVideos();
        movieVideos.setVideos(new ArrayList<>());

        movieDetail.setVideos(movieVideos);

        SimilarMovies similarMovies = new SimilarMovies();
        similarMovies.setMovies(new ArrayList<>());

        movieDetail.setSimilarMovies(similarMovies);

        MovieReviews movieReviews = new MovieReviews();
        movieReviews.setReviews(new ArrayList<>());

        movieDetail.setMovieReviews(movieReviews);

        return movieDetail;
    }
}
