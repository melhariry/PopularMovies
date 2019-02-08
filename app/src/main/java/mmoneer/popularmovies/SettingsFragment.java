package mmoneer.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Mohammad Moneer on 9/24/2016.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
