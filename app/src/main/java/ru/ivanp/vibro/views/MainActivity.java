package ru.ivanp.vibro.views;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.utils.Navigation;
import ru.ivanp.vibro.utils.Pref;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.OnCheckedChangeListener;

/**
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class MainActivity extends BaseActivity implements Navigation.Callback {
    // ============================================================================================
    // FIELDS
    // ============================================================================================
    private Drawer drawer;

    // ============================================================================================
    // LIFECYCLE
    // ============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .addDrawerItems(
                        new SwitchDrawerItem().withName(R.string.vibration_enabled).withIcon(R.drawable.ic_power_white_48dp).withChecked(Pref.vibrationEnabled).withOnCheckedChangeListener(new OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                                Pref.vibrationEnabled = isChecked;
                                Pref.save(MainActivity.this);
                            }
                        }),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.main).withIcon(R.drawable.ic_home_white_48dp).withIdentifier(Navigation.Items.MAIN),
                        new PrimaryDrawerItem().withName(R.string.tap_recorder).withIcon(R.drawable.ic_hand_pointing_right_white_48dp).withIdentifier(Navigation.Items.TAP_RECORDER),
                        new PrimaryDrawerItem().withName(R.string.morse_recorder).withIcon(R.drawable.ic_dots_horizontal_white_48dp).withIdentifier(Navigation.Items.MORSE_RECORDER),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.settings).withIcon(R.drawable.ic_settings_white_48dp).withIdentifier(Navigation.Items.SETTINGS).withCheckable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        Navigation.navigate(drawerItem.getIdentifier(), MainActivity.this, MainActivity.this);
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState);
        if (App.DEBUG) {
            drawerBuilder.addDrawerItems(new SecondaryDrawerItem().withName("Test").withIcon(R.drawable.ic_bug_white_48dp).withIdentifier(Navigation.Items.TEST).withCheckable(false));
        }
        drawer = drawerBuilder.build();

        Bundle data = getIntent().getExtras();
        int fragmentId = Navigation.Items.MAIN;
        if (savedInstanceState != null) {
            fragmentId = savedInstanceState.getInt(Navigation.ITEM_KEY, Navigation.Items.MAIN);
        }
        if (data != null) {
            fragmentId = data.getInt(Navigation.ITEM_KEY, Navigation.Items.MAIN);
        }
        Navigation.navigate(fragmentId, this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.getPlayer().stop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = drawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
        outState.putInt(Navigation.ITEM_KEY, getSelectedDrawerItemId());
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else if (getSelectedDrawerItemId() != Navigation.Items.MAIN) {
            Navigation.navigate(Navigation.Items.MAIN, this, this);
        } else {
            super.onBackPressed();
        }
    }

    private int getSelectedDrawerItemId() {
        int position = drawer.getCurrentSelection();
        if (position != -1) {
            return drawer.getDrawerItems().get(position).getIdentifier();
        }
        return Navigation.Items.MAIN;
    }

    // ============================================================================================
    // NAVIGATION CALLBACK
    // ============================================================================================
    @Override
    public void showFragment(Fragment _fragment) {
        drawer.setSelectionByIdentifier(Navigation.getSelectedItemId(), false);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentLayout, _fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void back() {
        Navigation.navigate(Navigation.Items.MAIN, this, this);
    }
}