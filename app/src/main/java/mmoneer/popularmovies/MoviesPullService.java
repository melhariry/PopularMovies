package mmoneer.popularmovies;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
;


import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbReviews;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.tools.MovieDbException;
import mmoneer.popularmovies.data.Movie;
import mmoneer.popularmovies.data.MovieDbHelper;

/**
 * Created by Mohammad Moneer on 9/24/2016.
 */

public class MoviesPullService extends IntentService {

    public static final String API_KEY = "56b57c241306ce1417bbfa6d6ec49317";
    public static final String MOVIES_PAGE = "moviespage";
    public static final String MOVIE = "movie";
    public static final String RESULT = "result";
    public static final String REQ_TYPE = "request_type";
    public static final String POPULAR = "popular";
    public static final String POPULARITY = "popularity";
    public static final String TOP_RATED = "rating";
    public static final String FAVS = "fav";
    public static final String WITH_ID = "with_id";
    public static final String TRAILERS = "trailers";
    public static final String REVIEWS = "reviews";
    public static final String LANGUAGE = "en";
    public static final String ID = "id";
    public static final String TWO_PANE = "two_pane";
    public static final String ACTION = "mmoneer.popularmovies.MOVIES_RECEIVED";
    public static final String YOUTUBE_BASE_LINK = "https://www.youtube.com/watch?v=";
    private static int result;
    private Boolean mTwoPane;
    private String categoryString;
    private TmdbMovies movies;
    private TmdbReviews moviesReviews;
    public MoviesPullService() {

        super(MoviesPullService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        categoryString = workIntent.getStringExtra(REQ_TYPE);
        mTwoPane = workIntent.getBooleanExtra(TWO_PANE, false);
        int favId = workIntent.getIntExtra(ID, 0);
        if (!categoryString.equals(FAVS) && !isOnline()) {
            result = Activity.RESULT_CANCELED;
            publishResult(result, favId);
            return;
        }
        result = Activity.RESULT_OK;
        if (!categoryString.equals(FAVS)) {
            try {
                movies = new TmdbApi(API_KEY).getMovies();
                moviesReviews = new TmdbApi(API_KEY).getReviews();
            }
            catch (MovieDbException ex) {
                publishResult(4,0);
                return;
            }
        }
        MovieResultsPage moviesPage = null;
        MovieDb movieWithId = null;
        ArrayList<String> movieTrailers = new ArrayList<>();
        TmdbReviews.ReviewResultsPage movieReviews = null;
        switch (categoryString) {
            case POPULAR:
                moviesPage =  movies.getPopularMovies(LANGUAGE, 1);
                break;
            case POPULARITY:
                moviesPage =  movies.getPopularMovies(LANGUAGE, 1);
                break;
            case TOP_RATED:
                moviesPage =  movies.getTopRatedMovies(LANGUAGE, 1);
                break;
            case FAVS:
                result = 10;
                break;
            case WITH_ID:
                int id = workIntent.getIntExtra(ID, 0);
                if (id != 0){
                    movieWithId = movies.getMovie(id, LANGUAGE);
                    List<Video> trailers = movies.getVideos(id, LANGUAGE);
                    for (Video t :trailers) {
                        movieTrailers.add(YOUTUBE_BASE_LINK + t.getKey());
                    }
                    movieReviews = moviesReviews.getReviews(id, LANGUAGE, 1);
                }

                else
                    result = Activity.RESULT_CANCELED;
                break;
            default:
                result = Activity.RESULT_FIRST_USER;
                break;
        }

        if (mTwoPane && !categoryString.equals(FAVS)) {
            int id = moviesPage.getResults().get(0).getId();
            movieWithId = movies.getMovie(id, LANGUAGE);
            List<Video> trailers = movies.getVideos(id, LANGUAGE);
            for (Video t :trailers) {
                movieTrailers.add(YOUTUBE_BASE_LINK + t.getKey());
            }
            movieReviews = moviesReviews.getReviews(id, LANGUAGE, 1);

        }

        if (moviesPage != null && movieWithId != null)
            publishAll(moviesPage, movieWithId, movieTrailers, movieReviews, result);

        else if (moviesPage != null)
            publishMovies(moviesPage, result);

        else if (movieWithId != null)
            publishMovieWithId(movieWithId, movieTrailers, movieReviews, result);

        else
            publishResult(result, favId);
    }

    private void publishAll(MovieResultsPage moviesPage, MovieDb movie, ArrayList<String> movieTrailers, TmdbReviews.ReviewResultsPage movieReviews,  int result) {
        Intent intent = new Intent();
        intent.putExtra(MOVIE, movie);
        intent.putExtra(MOVIES_PAGE, moviesPage);
        intent.putExtra(RESULT, result);
        intent.putExtra(REQ_TYPE, categoryString);
        intent.putStringArrayListExtra(TRAILERS, movieTrailers);
        intent.putExtra(REVIEWS, movieReviews);
        intent.putExtra(TWO_PANE, mTwoPane);
        intent.setAction(ACTION);
        sendBroadcast(intent);
    }

    private void publishMovies(MovieResultsPage moviesPage, int result) {
        Intent intent = new Intent();
        intent.putExtra(MOVIES_PAGE, moviesPage);
        intent.putExtra(RESULT, result);
        intent.putExtra(REQ_TYPE, categoryString);
        intent.setAction(ACTION);
        sendBroadcast(intent);
    }

    private void publishMovieWithId(MovieDb movie, ArrayList<String> movieTrailers, TmdbReviews.ReviewResultsPage movieReviews,  int result) {
        Intent intent = new Intent();
        intent.putExtra(MOVIE, movie);
        intent.putExtra(RESULT, result);
        intent.putStringArrayListExtra(TRAILERS, movieTrailers);
        intent.putExtra(REVIEWS, movieReviews);
        intent.putExtra(REQ_TYPE, categoryString);
        intent.setAction(ACTION);
        sendBroadcast(intent);
    }

    private void publishResult(int result, int id) {
        Intent intent = new Intent();
        intent.putExtra(RESULT, result);
        intent.putExtra(TWO_PANE, mTwoPane);
        if (id != 0)
            intent.putExtra(ID, id);
        intent.setAction(ACTION);
        sendBroadcast(intent);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
