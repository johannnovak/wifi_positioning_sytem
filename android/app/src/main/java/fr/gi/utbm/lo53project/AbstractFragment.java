package fr.gi.utbm.lo53project;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by celian on 05/05/15 for LO53Project
 */
public abstract class AbstractFragment extends Fragment {

    protected WorldMap mMap;
    protected static String mServerIP = "192.168.1.82";
    protected String mMacAddress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Load the world map from Main Activity
        Bundle args = getArguments();
        mMap = (WorldMap) args.getSerializable(MainActivity.TAG_WORLDMAP);

        mMacAddress = getMacAddress(getActivity());
        return null;
    }

    private String getMacAddress(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wifimanager.getConnectionInfo().getMacAddress();
        if (macAddress == null) {
            macAddress = "Device don't have mac address or wi-fi is disabled";

        }
        return macAddress;
    }

}

