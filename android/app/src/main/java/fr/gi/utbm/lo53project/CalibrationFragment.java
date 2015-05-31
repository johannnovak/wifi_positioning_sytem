package fr.gi.utbm.lo53project;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Android on 06/04/2015 for LO53Project
 */
public class CalibrationFragment extends AbstractFragment {

    private PointF mPointWaitingForValidation;

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
                // Enable user to select send button
                setHasOptionsMenu(true);

                // Get the current selected point
                mPointWaitingForValidation = new PointF(x, y);
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
            // Open a client socket
            Socket clientSocket = new Socket(mServerIP, mServerPort);

            // Send calibration position to the server
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(mMacAddress + ";" + (int)x + ";" + (int)y + ";"); // "mobileMacAddress;x;y"

            try {
                // Wait for an answer from the server
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                String code = (String) ois.readObject();

                // Check server answer
                if (code.equals("200")) {
                    Toast.makeText(getActivity(), "Position " + new PointF(x, y).toString()+ " sent !", Toast.LENGTH_SHORT).show();
                    mViewport.addPoint(x, y, Position.Type.CALIBRATION);
                }
                else {
                    Toast.makeText(getActivity(), "Server unable to receive that ... :(", Toast.LENGTH_SHORT).show();
                }

                // Close socket
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

    // the create options menu with a MenuInflater to have the menu from your fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calibration, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // If we clicked on the send action button
        if (item.getItemId() == R.id.action_send) {
            if (getResources().getBoolean(R.bool.using_server)) {
                sendPoint(mPointWaitingForValidation.x, mPointWaitingForValidation.y);
            }
            else {
                mViewport.addPoint(mPointWaitingForValidation.x, mPointWaitingForValidation.y, Position.Type.CALIBRATION);
                Toast.makeText(getActivity(), "Position " + mPointWaitingForValidation.toString() + " sent !", Toast.LENGTH_SHORT).show();
            }

            // We don't need anymore this point
            mPointWaitingForValidation = null;

            // User can't use anymore the send action until he'll make a new selection
            setHasOptionsMenu(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
