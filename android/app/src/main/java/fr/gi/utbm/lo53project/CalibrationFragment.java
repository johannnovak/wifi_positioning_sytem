package fr.gi.utbm.lo53project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Android on 06/04/2015.
 */
public class CalibrationFragment extends AbstractFragment {

    private Button add_calib_button;
    private EditText x_calib_textedit;
    private EditText y_calib_textedit;

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

        // Get buttons and textedits
        add_calib_button = (Button) rootView.findViewById(R.id.add_calib_button);
        x_calib_textedit = (EditText) rootView.findViewById(R.id.x_calib_textedit);
        y_calib_textedit = (EditText) rootView.findViewById(R.id.y_calib_textedit);

        // When user add a position
        add_calib_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                float x = Float.valueOf(x_calib_textedit.getText().toString());
                float y = Float.valueOf(y_calib_textedit.getText().toString());

                addPoint(x, y);

                if (!sendPoint(x, y))
                    System.out.println("Sent failed !");

            }
        });

        return rootView;
    }

    private void addPoint(float x, float y) {
        mMap.addPosition(x, y, Position.Type.CALIBRATION);
        mViewport.invalidate(); // Force the viewport to redraw
    }

    private boolean sendPoint(float x, float y) {
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
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
