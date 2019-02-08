package mmoneer.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import mmoneer.popularmovies.data.Movie;
import mmoneer.popularmovies.data.MovieDbHelper;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoviesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviesFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private View rootView;
    Boolean mTwoPane = false;
    private GridView moviesView;
    private MovieResultsPage moviesPage;
    private int mPosition = ListView.INVALID_POSITION;
    public static final String NO_CONN = "No Internet connection";
    private static final String SELECTED_KEY = "selected_position";
    private static final String TIMEOUT = "Connection Timeout";
    private static final String NO_FAVS ="No favorties to show";
    private int resultCode;
    List<Integer> IDs = new ArrayList<>();
    MovieDbHelper dbHelper;
    public MoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoviesFragment newInstance(String param1, String param2) {
        MoviesFragment fragment = new MoviesFragment();
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
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        moviesView = (GridView) rootView.findViewById(R.id.gridview_movies);
        dbHelper = MovieDbHelper.getInstance(getActivity());


        moviesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (moviesPage != null &&!mTwoPane) {
                    Intent toDetails = new Intent(getActivity(), DetailActivity.class);
                    toDetails.putExtra(MoviesPullService.ID, moviesPage.getResults().get(i).getId());
                    toDetails.putExtra(MoviesPullService.REQ_TYPE, MoviesPullService.WITH_ID);
                    toDetails.putExtra(MoviesPullService.TWO_PANE, mTwoPane);
                    startActivity(toDetails);

                }
                else if (resultCode == 10 && !mTwoPane) {
                    Intent toDetails = new Intent(getActivity(), DetailActivity.class);
                    toDetails.putExtra(MoviesPullService.ID, IDs.get(i));
                    toDetails.putExtra(MoviesPullService.REQ_TYPE, MoviesPullService.FAVS);
                    toDetails.putExtra(MoviesPullService.TWO_PANE, mTwoPane);
                    startActivity(toDetails);
                }
                else if (mTwoPane) {
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), MoviesPullService.class);
                    if (resultCode == 10) {
                        intent.putExtra(MoviesPullService.REQ_TYPE, MoviesPullService.FAVS);
                    }
                    else {
                        intent.putExtra(MoviesPullService.REQ_TYPE, MoviesPullService.WITH_ID);
                    }
                    intent.putExtra(MoviesPullService.ID, IDs.get(i));
                    getActivity().startService(intent);
                }
                mPosition = i;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (mPosition != ListView.INVALID_POSITION) {
            savedInstanceState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getApplicationContext().registerReceiver(MoviesBroadcastReceiver, new IntentFilter(MoviesPullService.ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getApplicationContext().unregisterReceiver(MoviesBroadcastReceiver);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private BroadcastReceiver MoviesBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                resultCode = bundle.getInt(MoviesPullService.RESULT);
                if (resultCode == 4) {
                    Toast.makeText(getActivity(), TIMEOUT, Toast.LENGTH_SHORT).show();
                    rootView.findViewById(R.id.loading_panel).setVisibility(View.GONE);
                    return;
                }
                moviesPage =  (MovieResultsPage) bundle.get(MoviesPullService.MOVIES_PAGE);
                if (!mTwoPane)
                    mTwoPane = bundle.getBoolean(MoviesPullService.TWO_PANE);

                if (resultCode == 10) {
                    List<Movie>  favMovies = dbHelper.getAllMovies();
                    List<byte[]> postersPath = new ArrayList<>();
                    for (Movie movie:favMovies) {
                        postersPath.add(movie.getPoster());
                        IDs.add(movie.getImdbId());

                    }
                    if (favMovies.size() == 0)
                        Toast.makeText(getActivity(), NO_FAVS, Toast.LENGTH_SHORT).show();

                    moviesView.setAdapter(new MoviesAdapter(getActivity(), null, postersPath, 1));
                }
                else if (resultCode == RESULT_OK && moviesPage != null) {

                    List<MovieDb> moviesList = moviesPage.getResults();
                    List<String> postersPath = new ArrayList<>();
                    IDs.clear();
                    for (MovieDb movie:moviesList) {
                        postersPath.add(movie.getPosterPath());
                        IDs.add(movie.getId());
                    }


                    moviesView.setAdapter(new MoviesAdapter(getActivity(), postersPath, null, 0));
                }
                else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getActivity(),NO_CONN, Toast.LENGTH_SHORT).show();
                }
                rootView.findViewById(R.id.loading_panel).setVisibility(View.GONE);
                if (mPosition != ListView.INVALID_POSITION)
                    moviesView.smoothScrollToPosition(mPosition);
            }
        }
    };

}
