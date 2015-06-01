package fr.gi.utbm.lo53project;

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


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static String TAG_WORLDMAP = "Global WorldMap";
    public static String TAG_WORLDMAP_BUNDLE = "Global WorldMap Bundle";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    
    private Bundle mWorldMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("MainActivity : Creating ...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

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
            mWorldMap = new Bundle();
            mWorldMap.putSerializable(TAG_WORLDMAP, new WorldMap());
        }

        System.out.println("MainActivity : Created !");
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBundle(TAG_WORLDMAP_BUNDLE, mWorldMap);
    }

    @Override
    public void onRestoreInstanceState (@NonNull Bundle savedInstanceState) {
        mWorldMap = savedInstanceState.getBundle(TAG_WORLDMAP_BUNDLE);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        System.out.println("##############################");
        System.out.println("MainActivity : Selecting item ...");
        Fragment objFragment = null;

        switch(position) {
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

        // Give the bundle (containing the WorldMap) to the fragment
        objFragment.setArguments(mWorldMap);

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, objFragment)
                .commit();

        System.out.println("MainActivity : Item " + mTitle + " selected !");
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setSubtitle(mTitle);
        actionBar.setTitle(getTitle());
    }


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

}
