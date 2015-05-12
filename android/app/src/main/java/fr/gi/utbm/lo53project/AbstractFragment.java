package fr.gi.utbm.lo53project;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;

/**
 * Created by celian on 05/05/15.
 */
public abstract class AbstractFragment extends Fragment {

    protected WorldMap mMap;
    protected URL mUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Load the world map from Main Activity
        Bundle args = getArguments();
        mMap = (WorldMap) args.getSerializable(MainActivity.TAG_WORLDMAP);

        return null;
    }

}

