package fr.gi.utbm.lo53project;

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
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Android on 06/04/2015 for LO53Project
 */
public class LocationFragment extends AbstractFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // the super function fetch mMap from bundle
        super.onCreateView(inflater, container, savedInstanceState);

        // Get server port from resources
        mServerPort = getActivity().getPreferences(MainActivity.PREFERENCE_MODE_PRIVATE).getInt(
                MainActivity.TAG_PREF_SERVER_PORT_LOCATION,
                getResources().getInteger(R.integer.server_port_location) // default value
        );

        // Initialize view
        View rootView = inflater.inflate(R.layout.location_layout, container, false);

        // Initialize viewport and add it to the linear layout
        mViewport = new LocationViewport(getActivity(), null, mMap);
        LinearLayout location_viewport_layout = (LinearLayout)rootView.findViewById(R.id.location_viewport_layout);
        location_viewport_layout.addView(mViewport);

        ReceiverAsyncTask receiver = new ReceiverAsyncTask();
        receiver.execute();

        return rootView;
    }

    public class ReceiverAsyncTask extends AsyncTask<Void, Position, Void> {

        private boolean mRun = true;

        //Background task which serve for the client
        @Override
        protected Void doInBackground(Void... params) {

            while(!this.isCancelled()) {

                if (mUsingServer) {
                    try {
                        // Get the accepted socket object
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(mServerIP, mServerPort), 500);
                        try {
                            String code = new String(IOUtils.toByteArray(socket.getInputStream()));

                            // Publish the received data
                            publishProgress(decode(code));

                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    catch (ConnectException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e) {

                    }
                }
                else {
                    try {
                        Thread.sleep(500);
                        publishProgress(
                            new Position(
                                (int)(Math.random()*5),
                                (int)(Math.random()*5)
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

        @Override
        protected void onProgressUpdate(Position... p) {
            super.onProgressUpdate(p);
            mViewport.addPosition(p[0].x, p[0].y, Position.Type.HOVER);
            Toast.makeText(getActivity(), "Position " + p[0].toString() + " received !", Toast.LENGTH_SHORT).show();
        }

        private Position decode (String code) {
            int x, y;

            String[] coordinates = code.split(";");
            x = Integer.getInteger(coordinates[0]);
            y = Integer.getInteger(coordinates[1]);
            return new Position(x, y);
        }
    }
}
