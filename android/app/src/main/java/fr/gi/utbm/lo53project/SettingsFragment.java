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
        SharedPreferences preferencesSettings = getActivity().getPreferences(MainActivity.PREFERENCE_MODE_PRIVATE);
        final SharedPreferences.Editor preferenceEditor = preferencesSettings.edit();

        final CheckBox using_server = (CheckBox) rootView.findViewById(R.id.checkBox_using_server);
        final EditText server_ip = (EditText) rootView.findViewById(R.id.editText_server_ip);
        final EditText server_port_location = (EditText) rootView.findViewById(R.id.editText_server_port_location);
        final EditText server_port_calibration = (EditText) rootView.findViewById(R.id.editText_server_port_calibration);
        Button apply_button = (Button) rootView.findViewById(R.id.button_apply_settings);

        // Initialize settings with default server properties
        using_server.setChecked(getResources().getBoolean(R.bool.using_server));
        server_ip.setText(getResources().getString(R.string.server_ip));
        int port_location = getResources().getInteger(R.integer.server_port_location);
        server_port_location.setText(Integer.toString(port_location));
        int port_calibration = getResources().getInteger(R.integer.server_port_calibration);
        server_port_calibration.setText(Integer.toString(port_calibration));

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
                preferenceEditor.apply();
            }
        });

        return rootView;
    }
}
