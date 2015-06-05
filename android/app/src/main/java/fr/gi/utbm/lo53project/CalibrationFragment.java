package fr.gi.utbm.lo53project;

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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Android on 06/04/2015 for LO53Project
 */
public class CalibrationFragment extends AbstractFragment {

    private Square mSquareWaitingForValidation;

    /**
     * {@inheritDoc}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Calibration fragment : Creating view...");

        // the super function fetch mMap from bundle
        super.onCreateView(inflater, container, savedInstanceState);

        // Get server port from preferences
        mServerPort = getActivity().getPreferences(MainActivity.PREFERENCE_MODE_PRIVATE).getInt(
                MainActivity.TAG_PREF_SERVER_PORT_CALIBRATION,
                getResources().getInteger(R.integer.server_port_calibration) // default value
        );

        // Initialize view
        View rootView = inflater.inflate(R.layout.calibration_layout, container, false);

        // Initialize viewport and its listener
        mViewport = new CalibrationViewport(getActivity(), null, mMap, new AbstractViewport.SelectionListener() {
            @Override
            public void onSelect(int x, int y) {
                startWaitingForValidation(x, y);
            }
        });

        // Add the viewport to the linear layout
        LinearLayout calibration_viewport_layout = (LinearLayout)rootView.findViewById(R.id.calib_viewport_layout);
        calibration_viewport_layout.addView(mViewport);

        System.out.println("Calibration fragment : View created !");
        return rootView;
    }

    /**
     * Enable fragment to use send & cancel buttons
     * @param menu menu
     * @param inflater menu inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calibration, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Choose a handler according to the button clicked (i.e. options item selected)
     * @param item options item selected
     * @return true if all is fine
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // If we clicked on the send action button
        if (item.getItemId() == R.id.action_send) {

            handleSendButton();
            stopWaitingForValidation ();
            return true;
        }

        // If we clicked on the cancel action button
        if (item.getItemId() == R.id.action_cancel) {

            handleCancelButton();
            stopWaitingForValidation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle the sending
     */
    private void handleSendButton() {

        // Return true if we are not using the server, else return the send point result
        boolean sent = !mUsingServer || sendPoint(mSquareWaitingForValidation.x, mSquareWaitingForValidation.y);

        // If position have been sent correctly, we add the point to the viewport
        if (sent) {
            mViewport.addSquare(mSquareWaitingForValidation.x, mSquareWaitingForValidation.y, Square.Type.CALIBRATION);
            Toast.makeText(getActivity(), "Position " + mSquareWaitingForValidation.toString() + " sent !", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle the sending cancel
     */
    private void handleCancelButton() {
        Toast.makeText(getActivity(), "Position " + mSquareWaitingForValidation.toString() + " cancelled !", Toast.LENGTH_SHORT).show();
    }

    /**
     * Start waiting than the user make his choice between :
     *  - select send button to send the current square
     *  - select cancel button to cancel sending
     *  - select another square to send
     *
     *  Coordinates are used to initialize the current square which wait for validation
     * @param x x coordinate of the square
     * @param y y coordinate of the square
     */
    private void startWaitingForValidation (int x, int y) {

        // World map start waiting (fix the current hover point)
        mMap.startWaiting();

        // Save the current selected square
        mSquareWaitingForValidation = new Square(x, y);

        // Enable user to select buttons
        setHasOptionsMenu(true);
    }

    /**
     * Stop waiting, the map can release the hovered square and the current square is removed
     */
    private void stopWaitingForValidation () {

        // World map stop waiting
        mMap.stopWaiting();

        // We remove the waiting point
        mSquareWaitingForValidation = null;

        // User can't use buttons anymore until he'll make a new selection
        setHasOptionsMenu(false);
    }

    /**
     * Send a point to the server and verify the result
     * @param x x coordinate
     * @param y y coordinate
     * @return a boolean which defines if the point have been sent
     */
    private boolean sendPoint(int x, int y) {

        // Open a client socket
        Socket clientSocket = new Socket();

        try {
            clientSocket.connect(new InetSocketAddress(mServerIP, mServerPort), 20000);

            // Send calibration position to the server "mobileMacAddress;x;y"
            clientSocket.getOutputStream().write((mMacAddress + ";" + x + ";" + y + ";").getBytes());

            String code = new String(IOUtils.toByteArray(clientSocket.getInputStream()));

            Toast.makeText(getActivity(), "Server answer is : " + code, Toast.LENGTH_SHORT).show();

            // Close socket
            clientSocket.close();

            // Check server answer
            return code.equals("200");

        }
        catch (IOException e) {
            Toast.makeText(getActivity(), "IOException : unable to make a connection with the server.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return false;
    }
}
