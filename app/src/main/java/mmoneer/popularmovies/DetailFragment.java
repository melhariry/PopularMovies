package mmoneer.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbReviews;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Reviews;
import mmoneer.popularmovies.data.Movie;
import mmoneer.popularmovies.data.MovieDbHelper;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class DetailFragment extends Fragment {


    private static final String FORMAT_RATING = "/10";
    private static final String FORMAT_DUR= " mins";
    private static final String REMOVED = "Removed from favorites";
    private static final String ADDED = "Added to favorites";
    private Button addFavButton;
    private MovieDb selectedMovie;
    private TmdbReviews.ReviewResultsPage movieReviews;
    private ArrayList<String> movieTrailers;
    private ListView trailersListView, reviewsListView;
    private View rootView;
    private int mId;
    private MovieDbHelper dbHelper;
    private Movie SelectedMovie;
    private boolean mTwoPane;
    private TextView titleTextView, yearTextView, durTextView, ratingTextView, descTextView;
    private ImageView movieIcon;
    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        trailersListView = (ListView) rootView.findViewById(R.id.listview_trailers);
        titleTextView = (TextView) rootView.findViewById(R.id.movie_name_textview);
        yearTextView = (TextView)rootView.findViewById(R.id.movie_year_textview);
        movieIcon = (ImageView) rootView.findViewById(R.id.movie_icon);
        durTextView = (TextView)rootView.findViewById(R.id.movie_dur_textview);
        ratingTextView = (TextView)rootView.findViewById(R.id.movie_rating_textview);
        descTextView = (TextView)rootView.findViewById(R.id.movie_desc_textview);
        reviewsListView = (ListView) rootView.findViewById(R.id.listview_reviews);



        addFavButton = (Button)rootView.findViewById(R.id.add_favorite_button);
        dbHelper = MovieDbHelper.getInstance(getActivity());

        addFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHelper.getMovie(mId) == null) {
                    addFavButton.setText(R.string.remove_favorite);

                    ArrayList<String> userNames = new ArrayList<>();
                    ArrayList<String> reviewContents = new ArrayList<>();
                    if (movieReviews != null) {
                        for (Reviews r : movieReviews) {
                            userNames.add(r.getAuthor());
                            reviewContents.add(r.getContent());
                        }
                    }

                    ImageView image = (ImageView) rootView.findViewById(R.id.movie_icon);
                    Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] poster = stream.toByteArray();
                    Movie newMovie = null;
                    if (selectedMovie != null)
                    newMovie = new Movie(selectedMovie.getTitle(), selectedMovie.getOverview(), selectedMovie.getVoteAverage(),selectedMovie.getReleaseDate(),1,poster,selectedMovie.getId(),userNames,reviewContents, movieTrailers, selectedMovie.getRuntime());
                    else if (SelectedMovie != null)
                        newMovie = new Movie(SelectedMovie.getTitle(), SelectedMovie.getPlot(), SelectedMovie.getRating(),SelectedMovie.getReleaseDate(),1,poster,SelectedMovie.getImdbId(),SelectedMovie.getUserNames(),SelectedMovie.getReviewContents(), SelectedMovie.getTrailerList(), SelectedMovie.getRunTime());
                    if (newMovie != null) {
                        dbHelper.addMovie(newMovie);
                        dbHelper.addToFavourites(newMovie);
                        Toast.makeText(getActivity(), ADDED, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    addFavButton.setText(R.string.add_favorite);
                    dbHelper.removeMovie(mId);
                    Toast.makeText(getActivity(),REMOVED, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), MoviesPullService.class);
                    intent.putExtra(MoviesPullService.REQ_TYPE, MoviesPullService.FAVS);
                    intent.putExtra(MoviesPullService.TWO_PANE, mTwoPane);
                    getActivity().startService(intent);


                }
            }
        });

        trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent openTrailerIntent = new Intent(Intent.ACTION_VIEW);
                if (movieTrailers != null)
                    openTrailerIntent.setData(Uri.parse(movieTrailers.get(i)));
                else
                    openTrailerIntent.setData(Uri.parse(SelectedMovie.getTrailerList().get(i)));
                startActivity(openTrailerIntent);
            }
        });
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getApplicationContext().registerReceiver(MovieBroadcastReceiver, new IntentFilter(MoviesPullService.ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getApplicationContext().unregisterReceiver(MovieBroadcastReceiver);
    }


    private BroadcastReceiver MovieBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(MoviesPullService.RESULT);
                selectedMovie =  (MovieDb) bundle.get(MoviesPullService.MOVIE);
                if (!mTwoPane)
                    mTwoPane = bundle.getBoolean(MoviesPullService.TWO_PANE, false);
                if (resultCode == 4) {
                    return;
                }
                if (resultCode == 10) {
                    mId = bundle.getInt(MoviesPullService.ID);
                    if (mId == 0 && mTwoPane) {
                        List<Movie> favMovies = dbHelper.getAllMovies();
                        if (favMovies.size() > 0)
                            mId = favMovies.get(0).getImdbId();
                    }
                    Movie currentMovie = dbHelper.getMovie(mId);
                    if (currentMovie != null) {
                        String movieTitle = currentMovie.getTitle();
                        titleTextView.setText(movieTitle);

                        byte[] poster = currentMovie.getPoster();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(poster, 0, poster.length);

                        movieIcon.setImageBitmap(bitmap);
                        yearTextView.setText(currentMovie.getReleaseDate().substring(0, 4));
                        durTextView.setText(String.valueOf(currentMovie.getRunTime()) + FORMAT_DUR);
                        ratingTextView.setText(String.valueOf(((float) currentMovie.getRating())) + FORMAT_RATING);
                        descTextView.setText(currentMovie.getPlot());
                        trailersListView.setAdapter(new TrailersAdapter(getActivity(), currentMovie.getTrailerList()));
                        reviewsListView.setAdapter(new ReviewsAdapter(getActivity(), null, currentMovie.getUserNames(), currentMovie.getReviewContents(), 1));

                        setListViewHeightBasedOnChildren(trailersListView);
                    }
                    else if (mTwoPane) {
                        rootView.setVisibility(LinearLayout.GONE);
                    }
                }
                else if (resultCode == RESULT_OK && selectedMovie != null) {

                    mId = selectedMovie.getId();
                    movieReviews = (TmdbReviews.ReviewResultsPage) bundle.get(MoviesPullService.REVIEWS);
                    movieTrailers = bundle.getStringArrayList(MoviesPullService.TRAILERS);

                    String movieTitle = selectedMovie.getTitle();

                    titleTextView.setText(movieTitle);
                    Picasso.with(getActivity()).load(MoviesAdapter.BASE_URI_342 + selectedMovie.getPosterPath()).into(movieIcon);
                    yearTextView.setText(selectedMovie.getReleaseDate().substring(0,4));
                    durTextView.setText(String.valueOf(selectedMovie.getRuntime()) + FORMAT_DUR);
                    ratingTextView.setText(String.valueOf(selectedMovie.getVoteAverage()) + FORMAT_RATING);
                    descTextView.setText(selectedMovie.getOverview());
                    trailersListView.setAdapter(new TrailersAdapter(getActivity(), movieTrailers));
                    reviewsListView.setAdapter(new ReviewsAdapter(getActivity(), movieReviews.getResults(),null, null, 0));

                    setListViewHeightBasedOnChildren(trailersListView);
                }
                else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getActivity(),MoviesFragment.NO_CONN, Toast.LENGTH_SHORT).show();
                    rootView.setVisibility(ScrollView.GONE);
                }

                SelectedMovie = dbHelper.getMovie(mId);

                if (SelectedMovie == null) {
                    addFavButton.setText(R.string.add_favorite);
                }
                else {
                    addFavButton.setText(R.string.remove_favorite);
                }
            }
        }
    };

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
