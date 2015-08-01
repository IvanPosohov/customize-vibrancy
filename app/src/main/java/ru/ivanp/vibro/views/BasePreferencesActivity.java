package ru.ivanp.vibro.views;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.ivanp.vibro.R;

/**
 * Base class for all non fullscreen activities
 */
public class BasePreferencesActivity extends AppCompatPreferenceActivity {
    // ============================================================================================
    // ACTIVITY LIFECYCLE
    // ============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // ============================================================================================
    // MENU
    // ============================================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}