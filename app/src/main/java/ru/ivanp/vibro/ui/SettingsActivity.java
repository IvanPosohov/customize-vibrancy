package ru.ivanp.vibro.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ru.ivanp.vibro.R;

/**
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class SettingsActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static void startActivity(Context _context) {
        _context.startActivity(new Intent(_context, SettingsActivity.class));
    }
}