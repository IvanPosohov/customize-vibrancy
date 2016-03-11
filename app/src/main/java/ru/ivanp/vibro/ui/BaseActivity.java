package ru.ivanp.vibro.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import ru.ivanp.vibro.R;

/**
 * @author Posohov Ivan (posohof@gmail.com)
 * Base class for all non fullscreen activities
 */
public class BaseActivity extends AppCompatActivity {
    // ============================================================================================
    // FIELDS
    // ============================================================================================
    protected Toolbar toolbar;

    // ============================================================================================
    // ACTIVITY LIFECYCLE
    // ============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, (ViewGroup) findViewById(R.id.root));
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