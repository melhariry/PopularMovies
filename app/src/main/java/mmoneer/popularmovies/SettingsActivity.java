package mmoneer.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Mohammad Moneer on 9/24/2016.
 */

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_SORT = "pref_settings";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}
