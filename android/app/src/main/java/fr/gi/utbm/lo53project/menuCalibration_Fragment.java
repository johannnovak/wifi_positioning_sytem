package fr.gi.utbm.lo53project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Android on 06/04/2015.
 */
public class menuCalibration_Fragment extends Fragment {
    View rootView;
    private Viewport canvas;
    private Button add_calib_button;
    private EditText x_calib_textedit;
    private EditText y_calib_textedit;
    private URL url;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.menucalibration_layout, container, false);
        canvas = (Viewport) rootView.findViewById(R.id.calibration_canvas);
        canvas.drawPoint(50f, 50f);
        canvas.drawPoint(100f, 100f);
        canvas.drawPoint(200f, 200f);

        try {
//            url = new URL("http://192.168.2.135:8080/wifi_positioning/calibrate");
            url = new URL("http://192.168.43.78:8080/wifi_positioning/calibrate");
//            url = new URL("http://192.168.2.135:8080/wifi_positioning/locate");
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        add_calib_button = (Button) rootView.findViewById(R.id.add_calib_button);
        x_calib_textedit = (EditText) rootView.findViewById(R.id.x_calib_textedit);
        y_calib_textedit = (EditText) rootView.findViewById(R.id.y_calib_textedit);

        add_calib_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                float x = Float.valueOf(x_calib_textedit.getText().toString());
                float y = Float.valueOf(y_calib_textedit.getText().toString());


                try {
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.addRequestProperty("x", Float.toString(x));
                    connection.addRequestProperty("y", Float.toString(y));
                    connection.connect();

                    System.out.println(connection.getResponseCode());
                    if (connection.getResponseCode() == 200) {
                        canvas.drawPoint(x, y);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        return rootView;
    }

    public void clearCanvas(View v) {
        canvas.clearCanvas();
    }
}
