package ru.ivanp.vibro.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import ru.ivanp.vibro.R;

/**
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup root = (ViewGroup) findViewById(R.id.root);
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(layoutResID, root, true);
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