package mmoneer.popularmovies;


import android.content.Intent;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private String selectedPref;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        String selectedPref = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_PREF_SORT, "");
        if (!selectedPref.equals("")) {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, MoviesPullService.class);
            intent.putExtra(MoviesPullService.TWO_PANE, mTwoPane);
            intent.putExtra(MoviesPullService.REQ_TYPE, selectedPref);
            startService(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        String newPref = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.KEY_PREF_SORT, "");
        if (!newPref.equals("") && !newPref.equals(selectedPref)) {
            selectedPref = newPref;
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, MoviesPullService.class);
            intent.putExtra(MoviesPullService.REQ_TYPE, newPref);
            startService(intent);
        }
        else if (newPref.equals(MoviesPullService.FAVS)) {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, MoviesPullService.class);
            intent.putExtra(MoviesPullService.REQ_TYPE, newPref);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}