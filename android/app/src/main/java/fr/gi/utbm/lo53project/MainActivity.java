package fr.gi.utbm.lo53project;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Bundle's tags
     */
    public static String TAG_WORLDMAP = "Global WorldMap";
    public static String TAG_WORLDMAP_BUNDLE = "Global WorldMap Bundle";

    /**
     * Preference's tags
     */
    public static String TAG_PREF_USING_SERVER = "Using Server Boolean";
    public static String TAG_PREF_SERVER_IP = "Server IP String";
    public static String TAG_PREF_SERVER_PORT_LOCATION = "ServerLocation Port Integer";
    public static String TAG_PREF_SERVER_PORT_CALIBRATION = "Server Calibration Port Integer";
    public static String TAG_PREF_DEFAULT_MAP_WIDTH = "Default Map Width";
    public static String TAG_PREF_DEFAULT_MAP_HEIGHT = "Default Map Height";

    public static final int PREFERENCE_MODE_PRIVATE = 0;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Used to save the map when the instance is saved
     */
    private Bundle mMapSaveBundle;

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("MainActivity : Creating ...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null || !savedInstanceState.containsKey(TAG_WORLDMAP_BUNDLE)) {
            mMapSaveBundle = new Bundle();

            // Initialize the fragment container with settings
            if (findViewById(R.id.container) != null) {

                // Create a new Fragment to be placed in the activity layout
                SettingsFragment settings_frag = new SettingsFragment();

                // Replace the empty 'container' FrameLayout to the settings one
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, settings_frag).commit();

            }
        }

        System.out.println("MainActivity : Created !");
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBundle(TAG_WORLDMAP_BUNDLE, mMapSaveBundle);
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    public void onRestoreInstanceState (@NonNull Bundle savedInstanceState) {
        mMapSaveBundle = savedInstanceState.getBundle(TAG_WORLDMAP_BUNDLE);
    }

    /**
     * {@inheritDoc}
     * @param index
     */
    @Override
    public void onNavigationDrawerItemSelected(int index) {

        System.out.println("##############################");
        System.out.println("MainActivity : Selecting item ...");

        // Force to close the keyboard
        hideKeyboard();

        Fragment objFragment = null;

        switch(index) {
            case 0 :
                mTitle = getString(R.string.title_section1);
                objFragment = new CalibrationFragment();
                break;
            case 1 :
                mTitle = getString(R.string.title_section2);
                objFragment = new LocationFragment();
                break;
        }

        restoreActionBar();

        // Initialize world map and its bundle
        if(mMapSaveBundle.size() == 0) {
            int width = getPreferences(PREFERENCE_MODE_PRIVATE).getInt(TAG_PREF_DEFAULT_MAP_WIDTH, 3);
            int height = getPreferences(PREFERENCE_MODE_PRIVATE).getInt(TAG_PREF_DEFAULT_MAP_HEIGHT, 3);
            mMapSaveBundle.putSerializable(TAG_WORLDMAP, new WorldMap(width, height));
        }

        // Give the bundle (containing the WorldMap) to the fragment
        if (objFragment != null)
            objFragment.setArguments(mMapSaveBundle);

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment)
                .commit();

        System.out.println("MainActivity : Item " + mTitle + " selected !");
    }

    /**
     * Restore title and subtitle of the action bar
     */
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setSubtitle(mTitle);
        actionBar.setTitle(getTitle());
    }


    /**
     * {@inheritDoc}
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.calibration, menu);
            menu.findItem(R.id.action_send).setVisible(false);
            menu.findItem(R.id.action_cancel).setVisible(false);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Force to close the keyboard
     */
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
