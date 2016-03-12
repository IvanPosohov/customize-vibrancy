package ru.ivanp.vibro.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import ru.ivanp.vibro.R;

/**
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class SettingsActivity extends BaseActivity {
    private static final String TITLE_KEY = "title";
    private static final String TYPE_KEY = "type";

    private static final int APP = 0;
    private static final int RECORDER = 1;
    private static final int MORSE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            int titleResId = data.getInt(TITLE_KEY);
            setTitle(titleResId);

            int type = data.getInt(TYPE_KEY);
            Fragment fragment = null;
            switch (type) {
                case RECORDER:
                    fragment = RecorderSettingsFragment.newInstance();
                    break;
                case MORSE:
                    fragment = MorseSettingsFragment.newInstance();
                    break;
                default:
                    fragment = SettingsFragment.newInstance();
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentLayout, fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    public static void openAppPreferences(Context _context) {
        startActivity(_context, R.string.settings, APP);
    }

    public static void openRecorderPreferences(Context _context) {
        startActivity(_context, R.string.tap_recorder_settings, RECORDER);
    }

    public static void openMorsePreferences(Context _context) {
        startActivity(_context, R.string.morse_recorder_settings, MORSE);
    }

    private static void startActivity(Context _context, int _titleResId, int _type) {
        Intent intent = new Intent(_context, SettingsActivity.class);
        intent.putExtra(TITLE_KEY, _titleResId);
        intent.putExtra(TYPE_KEY, _type);
        _context.startActivity(intent);
    }
}