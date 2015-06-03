package fr.gi.utbm.lo53project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by celian on 02/06/15 for LO53Project
 */
public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // the super function fetch mMap from bundle
        super.onCreateView(inflater, container, savedInstanceState);

        // Initialize view
        View rootView = inflater.inflate(R.layout.settings_layout, container, false);

        // Get the preferences settings
        SharedPreferences prefs = getActivity().getPreferences(MainActivity.PREFERENCE_MODE_PRIVATE);
        final SharedPreferences.Editor preferenceEditor = prefs.edit();

        // Get from view the different elements
        final CheckBox using_server = (CheckBox) rootView.findViewById(R.id.checkBox_using_server);
        final EditText server_ip = (EditText) rootView.findViewById(R.id.editText_server_ip);
        final EditText server_port_location = (EditText) rootView.findViewById(R.id.editText_server_port_location);
        final EditText server_port_calibration = (EditText) rootView.findViewById(R.id.editText_server_port_calibration);
        final EditText default_map_width = (EditText) rootView.findViewById(R.id.editText_default_map_width);
        final EditText default_map_height = (EditText) rootView.findViewById(R.id.editText_default_map_height);
        Button apply_button = (Button) rootView.findViewById(R.id.button_apply_settings);

        // Default server properties - in case of preferences have not been set
        boolean using_server_def = getResources().getBoolean(R.bool.using_server);
        String server_ip_def = getResources().getString(R.string.server_ip);
        int server_port_location_def = getResources().getInteger(R.integer.server_port_location);
        int server_port_calibration_def = getResources().getInteger(R.integer.server_port_calibration);
        int default_map_width_def = 3;
        int default_map_height_def = 3;

        // Set values of gui elements according to either preferences or default values
        using_server.setChecked(prefs.getBoolean(
                MainActivity.TAG_PREF_USING_SERVER,
                using_server_def
        ));
        server_ip.setText(prefs.getString(
                MainActivity.TAG_PREF_SERVER_IP,
                server_ip_def
        ));
        server_port_location.setText(Integer.toString(prefs.getInt(
                MainActivity.TAG_PREF_SERVER_PORT_LOCATION,
                server_port_location_def
        )));
        server_port_calibration.setText(Integer.toString(prefs.getInt(
                MainActivity.TAG_PREF_SERVER_PORT_CALIBRATION,
                server_port_calibration_def
        )));
        default_map_width.setText(Integer.toString(prefs.getInt(
                MainActivity.TAG_PREF_DEFAULT_MAP_WIDTH,
                default_map_width_def
        )));
        default_map_height.setText(Integer.toString(prefs.getInt(
                MainActivity.TAG_PREF_DEFAULT_MAP_HEIGHT,
                default_map_height_def
        )));

        // Set the button listener
        apply_button.setOnClickListener(new View.OnClickListener() {
            /**
             * Edit preferences settings on click of the apply button
             * @param view
             */
            @Override
            public void onClick(View view) {
                preferenceEditor.putBoolean(MainActivity.TAG_PREF_USING_SERVER, using_server.isChecked());
                preferenceEditor.putString(MainActivity.TAG_PREF_SERVER_IP, server_ip.getText().toString());
                preferenceEditor.putInt(MainActivity.TAG_PREF_SERVER_PORT_LOCATION, Integer.parseInt(server_port_location.getText().toString()));
                preferenceEditor.putInt(MainActivity.TAG_PREF_SERVER_PORT_CALIBRATION, Integer.parseInt(server_port_calibration.getText().toString()));
                preferenceEditor.putInt(MainActivity.TAG_PREF_DEFAULT_MAP_WIDTH, Integer.parseInt(default_map_width.getText().toString()));
                preferenceEditor.putInt(MainActivity.TAG_PREF_DEFAULT_MAP_HEIGHT, Integer.parseInt(default_map_height.getText().toString()));
                preferenceEditor.apply();

                Toast.makeText(getActivity(), "Settings applied", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
