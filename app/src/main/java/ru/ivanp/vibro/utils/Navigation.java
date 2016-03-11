package ru.ivanp.vibro.utils;

import android.content.Context;
import android.support.v4.app.Fragment;

import ru.ivanp.vibro.ui.SettingsActivity;
import ru.ivanp.vibro.ui.MainFragment;
import ru.ivanp.vibro.ui.TestingActivity;

public final class Navigation {
    // ============================================================================================
    // INTERFACE
    // ============================================================================================
    public interface Callback {
        void showFragment(Fragment _fragment);

        void back();
    }

    // ============================================================================================
    // CONSTANTS
    // ============================================================================================
    public final class Items {
        public static final long MAIN = 0;
        public static final long TAP_RECORDER = 1;
        public static final long MORSE_RECORDER = 2;
        public static final long SETTINGS = 3;
        public static final long TEST = 4;
    }

    public static final String ITEM_KEY = "item_key";

    // ============================================================================================
    // FIELDS
    // ============================================================================================
    private static long selectedItemId;

    // ============================================================================================
    // GETTERS
    // ============================================================================================
    public static long getSelectedItemId() {
        return selectedItemId;
    }

    // ============================================================================================
    // METHODS
    // ============================================================================================
    public static void navigate(long _itemId, Context _context, Callback _handler) {
        selectedItemId = _itemId;
        if (_itemId == Items.MAIN) _handler.showFragment(MainFragment.newInstance());
            //else if (_itemId == Items.TAP_RECORDER) //_handler.showFragment(SelectCountryFragment.newInstance());
            //else if (_itemId == Items.MORSE_RECORDER) //_handler.showFragment(PaymentMethodFragment.newInstance());
        else if (_itemId == Items.SETTINGS) SettingsActivity.startActivity(_context);
        else if (_itemId == Items.TEST) TestingActivity.startActivity(_context);
    }

    private Navigation() {}
}
