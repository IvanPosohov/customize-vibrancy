package ru.ivanp.vibro.utils;

import android.content.Context;
import android.support.v4.app.Fragment;

import ru.ivanp.vibro.views.MainFragment;
import ru.ivanp.vibro.views.SettingsActivity;
import ru.ivanp.vibro.views.TestingActivity;

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
        public static final int MAIN = 0;
        public static final int TAP_RECORDER = 1;
        public static final int MORSE_RECORDER = 2;
        public static final int SETTINGS = 3;
        public static final int TEST = 4;
    }
    public static final String ITEM_KEY = "item_key";

    // ============================================================================================
    // FIELDS
    // ============================================================================================
    private static int selectedItemId;

    // ============================================================================================
    // GETTERS
    // ============================================================================================
    public static int getSelectedItemId() {
        return selectedItemId;
    }

    // ============================================================================================
    // METHODS
    // ============================================================================================
    public static void navigate(int _itemId, Context _context, Callback _handler) {
        selectedItemId = _itemId;
        switch (_itemId) {
            case Items.MAIN:
                _handler.showFragment(MainFragment.newInstance());
                break;
            case Items.TAP_RECORDER:
                //_handler.showFragment(SelectCountryFragment.newInstance());
                break;
            case Items.MORSE_RECORDER:
                //_handler.showFragment(PaymentMethodFragment.newInstance());
                break;
            case Items.SETTINGS:
                SettingsActivity.startActivity(_context);
                break;
            case Items.TEST:
                TestingActivity.startActivity(_context);
                break;
        }
    }

    private Navigation() {}
}
