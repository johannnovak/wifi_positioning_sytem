package fr.gi.utbm.lo53project;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ObjectInputStream;
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
//        mServerPort = getResources().getInteger(R.integer.server_port_location);
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

//        if (getResources().getBoolean(R.bool.using_server)) {
        if (mUsingServer) {
            ReceiverAsyncTask receiver = new ReceiverAsyncTask();
            receiver.execute();
        }

//        //New thread to listen to incoming connections
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
////                try {
////                    Socket clientSocket = new Socket(mServerIP, mServerPort);
//                Socket clientSocket = new Socket();
//
//                    while(true) {
////                        try {
////                            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
////                            String code = (String) ois.readObject();
//
//
//
//                            //For each client new instance of AsyncTask will be created
//                            ReceiverAsyncTask receiverAsyncTask = new ReceiverAsyncTask();
//                            //Start the AsyncTask execution
//                            //Accepted client socket object will pass as the parameter
//                            receiverAsyncTask.execute(new Socket[]{clientSocket});
//
////                          clientSocket.close();
////                        } catch (ClassNotFoundException e) {
////                            e.printStackTrace();
////                        }
//                    }
////                }
////                catch (IOException e) {
////                    e.printStackTrace();
////                }
//
//            }
//        }).start();

        return rootView;
    }

//    @SuppressWarnings("unused")
//    private void receivePoint() {
//        // TO DO
//        // - create a thread which loop ?
//        // - receiver already implemented
//
//        // use mViewport.addPoint(x, y, Position.Type.LOCATION);
//        try {
//            Socket clientSocket = new Socket(mServerIP, mServerPort);
//
////            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
////
////            oos.writeObject(mMacAddress + ";" + (int)x + ";" + (int)y + ";"); // "mobileMacAddress;x;y"
//
//            try {
//                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
//                String code = (String) ois.readObject();
//                PointF data = decode(code);
//                mViewport.addPoint(data.x, data.y, Position.Type.CALIBRATION);
//
//                clientSocket.close();
//            }
//            catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public class ReceiverAsyncTask extends AsyncTask<Void, PointF, Void> {

        private boolean mRun = true;

        //Background task which serve for the client
        @Override
        protected Void doInBackground(Void... params) {

            while(!this.isCancelled()) {
                try {
                    // Get the accepted socket object
                    Socket socket = new Socket(mServerIP, mServerPort);
                    try {
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        String code = (String) ois.readObject();

                        //TODO : thread.sleep

                        // Publish the received data
                        publishProgress(decode(code));

                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//
//                publishProgress(
//                    new PointF(
//                        500,
//                        1000
//                    )
//                );
//
//
//                publishProgress(
//                    new PointF(
//                        1000,
//                        1000
//                    )
//                );
//
//                publishProgress(
//                    new PointF(
//                        1000,
//                        1500
//                    )
//                );


            return null;
        }

        @Override
        protected void onProgressUpdate(PointF... p) {
            super.onProgressUpdate(p);
            //mViewport.addPoint(p[0].x, p[0].y, Position.Type.LOCATION);
            Toast.makeText(getActivity(), "Position " + p[0].toString() + " received !", Toast.LENGTH_SHORT).show();
        }

//        @Override
//        protected void onPostExecute(PointF p) {
////            if (p != null) {
////                mViewport.addPoint(p.x, p.y, Position.Type.LOCATION);
////            }
//        }

        private PointF decode (String code) {
            float x, y;

            String[] coordinates = code.split(";");
            x = Integer.getInteger(coordinates[0]);
            y = Integer.getInteger(coordinates[1]);
            return new PointF(x, y);
        }
    }
}
