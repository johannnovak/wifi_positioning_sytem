package fr.gi.utbm.lo53project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.net.URL;

/**
 * Created by Android on 06/04/2015.
 */
public class menuCalibration_Fragment extends Fragment {
    View rootView;
    private CanvasView canvas;
    private Button add_calib_button;
    private EditText x_calib_textedit;
    private EditText y_calib_textedit;
    private URL url;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.menucalibration_layout, container, false);
        canvas = (CanvasView) rootView.findViewById(R.id.calibration_canvas);
        canvas.drawPoint(50f, 50f);
        canvas.drawPoint(100f, 100f);
        canvas.drawPoint(200f, 200f);

//        try {
//            url = new URL("http://192.168.2.server:8080/wifiposition(n)ing/calibrate");
////            url = new URL("http://192.168.2.server:8080/wifiposition(n)ing/locate");
//        }
//        catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

        add_calib_button = (Button) rootView.findViewById(R.id.add_calib_button);
        x_calib_textedit = (EditText) rootView.findViewById(R.id.x_calib_textedit);
        y_calib_textedit = (EditText) rootView.findViewById(R.id.y_calib_textedit);

        add_calib_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                float x = Float.valueOf(x_calib_textedit.getText().toString());
                float y = Float.valueOf(y_calib_textedit.getText().toString());

                if (x != 0 && y != 0) {
                    canvas.drawPoint(x, y);
                }

//                try {
//                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
////                    connection.addRequestProperty("x", );
////                    connection.addRequestProperty("y", );
//                    connection.connect();
//                    if (connection.getResponseCode() == 200) {
////                        drawPoint
//                    }
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });



        return rootView;
    }

    public void clearCanvas(View v) {
        canvas.clearCanvas();
    }
}
