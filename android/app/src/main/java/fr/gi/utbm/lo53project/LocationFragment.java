package fr.gi.utbm.lo53project;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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
public class LocationFragment extends AbstractFragment {

    /**
     * Used in case of not using server, to display randomly between (0, 0) to (def_w, def_h)
     */
    private int default_width;
    private int default_height;

    /**
     * Asynchronous task which handle reception of squares
     */
    private ReceiverAsyncTask mReceiverTask;

    /**
     * {@inheritDoc}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // the super function fetch mMap from bundle
        super.onCreateView(inflater, container, savedInstanceState);

        SharedPreferences prefs = getActivity().getPreferences(MainActivity.PREFERENCE_MODE_PRIVATE);
        // Get server port from resources
        mServerPort = prefs.getInt(
                MainActivity.TAG_PREF_SERVER_PORT_LOCATION,
                getResources().getInteger(R.integer.server_port_location) // default value
        );

        default_width = prefs.getInt(
                MainActivity.TAG_PREF_DEFAULT_MAP_WIDTH,
                3
        );
        default_height = prefs.getInt(
                MainActivity.TAG_PREF_DEFAULT_MAP_HEIGHT,
                3
        );

        // Initialize view
        View rootView = inflater.inflate(R.layout.location_layout, container, false);

        // Initialize viewport and add it to the linear layout
        mViewport = new LocationViewport(getActivity(), null, mMap);
        LinearLayout location_viewport_layout = (LinearLayout)rootView.findViewById(R.id.location_viewport_layout);
        location_viewport_layout.addView(mViewport);

        return rootView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        mReceiverTask.cancel(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        // A task can be executed only once so we need to instantiate a new
        mReceiverTask = new ReceiverAsyncTask();
        mReceiverTask.execute();
    }

    /**
     * ReceiverTask : used to make the reception of data asynchronously
     * (otherwise, we could not drag and zoom the viewport at the same time)
     */
    public class ReceiverAsyncTask extends AsyncTask<Void, Square, Void> {

        /**
         * {@inheritDoc}
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Void... params) {

            while(!this.isCancelled()) {

                if (mUsingServer) {
                    try {
                        // Get the accepted socket object
                        Socket clientSocket = new Socket();
                        clientSocket.connect(new InetSocketAddress(mServerIP, mServerPort), 20000);
                        try {
                            // Send mobile mac address to the server
                            clientSocket.getOutputStream().write((mMacAddress).getBytes());

                            // Get the response code we have to decode to retrieve indices
                            String code = new String(IOUtils.toByteArray(clientSocket.getInputStream()));

                            // Publish the received data
                            publishProgress(decode(code));

                            clientSocket.close();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Sent or reception failed", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                    catch (IOException e) {
                        Toast.makeText(getActivity(), "Unable to connect to the server", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        Thread.sleep(500);

                        publishProgress(
                            new Square(
                                (int)(Math.random() * default_width),
                                (int)(Math.random() * default_height)
                            )
                        );
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * @param p
         */
        @Override
        protected void onProgressUpdate(Square... p) {
            super.onProgressUpdate(p);
            mViewport.addSquare(p[0].x, p[0].y, Square.Type.LOCATION);
        }

        /**
         * Decode the string code received in a square to display
         * The code format is quite simple : "x;y"
         * @param code string code
         * @return square to display
         */
        private Square decode (String code) {
            int x, y;

            String[] coordinates = code.split(";");
            x = Integer.getInteger(coordinates[0]);
            y = Integer.getInteger(coordinates[1]);
            return new Square(x, y);
        }
    }
}
