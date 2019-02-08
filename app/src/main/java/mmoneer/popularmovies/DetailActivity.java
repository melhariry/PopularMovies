package mmoneer.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_detail);

        DetailFragment fragment = new DetailFragment();
        int id = extras.getInt(MoviesPullService.ID);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.movie_detail_container, fragment)
                .commit();


        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, MoviesPullService.class);

        intent.putExtra(MoviesPullService.REQ_TYPE, extras.getString(MoviesPullService.REQ_TYPE));
        intent.putExtra(MoviesPullService.ID, id);
        startService(intent);
    }
}
