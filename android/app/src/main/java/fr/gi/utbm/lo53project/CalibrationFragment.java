package fr.gi.utbm.lo53project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Android on 06/04/2015 for LO53Project
 */
public class CalibrationFragment extends AbstractFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Calibration fragment : Creating view...");

        // the super function fetch mMap from bundle
        super.onCreateView(inflater, container, savedInstanceState);

        // Get server port from resources
        mServerPort = getResources().getInteger(R.integer.server_port_calibration);

        // Initialize view
        View rootView = inflater.inflate(R.layout.calibration_layout, container, false);

        // Initialize viewport and its listener
        mViewport = new CalibrationViewport(getActivity(), null, mMap, new AbstractViewport.SelectionListener() {
            @Override
            public void onSelect(float x, float y) {
//                sendPoint(x, y);
                mViewport.addPoint(x, y, Position.Type.CALIBRATION);
            }
        });

        // Add the viewport to the linear layout
        LinearLayout calibration_viewport_layout = (LinearLayout)rootView.findViewById(R.id.calib_viewport_layout);
        calibration_viewport_layout.addView(mViewport);

        System.out.println("Calibration fragment : View created !");
        return rootView;
    }

    /**
     * Send a point to the server and call addPoint if server return OK
     * @param x x coordinate
     * @param y y coordinate
     */
    private void sendPoint(float x, float y) {
        try {
            Socket clientSocket = new Socket(mServerIP, mServerPort);

            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            oos.writeObject(mMacAddress + ";" + (int)x + ";" + (int)y + ";"); // "mobileMacAddress;x;y"

            try {
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                String code = (String) ois.readObject();

                if (code.equals("200")) {
                    mViewport.addPoint(x, y, Position.Type.CALIBRATION);
                }
                clientSocket.close();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
