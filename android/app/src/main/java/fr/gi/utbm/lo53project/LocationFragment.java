package fr.gi.utbm.lo53project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Android on 06/04/2015 for LO53Project
 */
public class LocationFragment extends AbstractFragment {

    private LocationViewport mViewport;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // the super function fetch mMap from bundle
        super.onCreateView(inflater, container, savedInstanceState);

        // Initialize view
        View rootView = inflater.inflate(R.layout.location_layout, container, false);

        // Initialize viewport and add it to the linear layout
        mViewport = new LocationViewport(getActivity(), null, mMap);
        LinearLayout location_viewport_layout = (LinearLayout)rootView.findViewById(R.id.location_viewport_layout);
        location_viewport_layout.addView(mViewport);

        return rootView;
    }

    private void receivePoint() {
        // TO DO
        // - create a thread which loop ?
        // - receiver already implemented

        // use mViewport.addPoint(x, y, Position.Type.LOCATION);
    }

}
