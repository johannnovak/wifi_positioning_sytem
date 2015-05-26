package fr.gi.utbm.lo53project;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
        mServerPort = getResources().getInteger(R.integer.server_port_location);

        // Initialize view
        View rootView = inflater.inflate(R.layout.location_layout, container, false);

        // Initialize viewport and add it to the linear layout
        mViewport = new LocationViewport(getActivity(), null, mMap);
        LinearLayout location_viewport_layout = (LinearLayout)rootView.findViewById(R.id.location_viewport_layout);
        location_viewport_layout.addView(mViewport);

        //New thread to listen to incoming connections
        new Thread(new Runnable() {

            @Override
            public void run() {
//                try {
//                    Socket clientSocket = new Socket(mServerIP, mServerPort);
                Socket clientSocket = new Socket();

                    while(true) {
//                        try {
//                            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
//                            String code = (String) ois.readObject();



                            //For each client new instance of AsyncTask will be created
                            ReceiverAsyncTask receiverAsyncTask = new ReceiverAsyncTask();
                            //Start the AsyncTask execution
                            //Accepted client socket object will pass as the parameter
                            receiverAsyncTask.execute(new Socket[]{clientSocket});

//                          clientSocket.close();
//                        } catch (ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
                    }
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        }).start();

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

    public class ReceiverAsyncTask extends AsyncTask<Socket, Void, PointF> {
        //Background task which serve for the client
        @Override
        protected PointF doInBackground(Socket... params) {
            PointF result = null;

            // Get the accepted socket object
//            Socket socket = params[0];
//            try {
//                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//                String code = (String) ois.readObject();
//                result = decode(code);
//
//                socket.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            result = new PointF(
                    (int)Math.random() * 1000,
                    (int)Math.random() * 1000);

            return result;
        }

        @Override
        protected void onPostExecute(PointF p) {
//            if (p != null) {
//                mViewport.addPoint(p.x, p.y, Position.Type.LOCATION);
//            }
        }

        private PointF decode (String code) {
            //TODO : decode the string received
            float x, y;
            x = 0;
            y = 0;
            return new PointF(x, y);
        }
    }
}
