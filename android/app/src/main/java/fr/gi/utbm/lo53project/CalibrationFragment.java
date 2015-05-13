package fr.gi.utbm.lo53project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Android on 06/04/2015 for LO53Project
 */
public class CalibrationFragment extends AbstractFragment {

    private CalibrationViewport mViewport;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // the super function fetch mMap from bundle
        super.onCreateView(inflater, container, savedInstanceState);

        // Initialize view
        View rootView = inflater.inflate(R.layout.calibration_layout, container, false);

        // Initialize viewport and add it to the linear layout
        mViewport = new CalibrationViewport(getActivity(), null, mMap);
        LinearLayout calibration_viewport_layout = (LinearLayout)rootView.findViewById(R.id.calib_viewport_layout);
        calibration_viewport_layout.addView(mViewport);

        // Initialize calibration's URL
        try {
            mUrl = new URL("http://192.168.43.78:8080/wifi_positioning/calibrate");
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mViewport.setOnSelectListener(new AbstractViewport.SelectListener() {

            @Override
            public void onSelect(float x, float y) {
                sendPoint(x, y);

                // To remove when we can send to server
                addPoint(x, y);
            }

        });

        return rootView;
    }

    /**
     * Add a point to the world map and force the viewport to redraw
     * @param x x coordinate
     * @param y y coordinate
     */
    private void addPoint(float x, float y) {
        mMap.addPosition(x, y, Position.Type.CALIBRATION);
        mViewport.invalidate(); // Force the viewport to redraw
    }

    /**
     * Send a point to the server and call addPoint if server return OK
     * @param x x coordinate
     * @param y y coordinate
     */
    private void sendPoint(float x, float y) {
        try {
            // Connection to the server
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();

            // Sending coordinates
            connection.addRequestProperty("x", Float.toString(x));
            connection.addRequestProperty("y", Float.toString(y));
            connection.connect();

            // If server return ok we add the point to the world map
            if (connection.getResponseCode() == 200) {
                addPoint(x, y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
