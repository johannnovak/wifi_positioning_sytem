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
    protected boolean mUsingServer;
    protected String mServerIP;
    protected int mServerPort;
    protected String mMacAddress;
    protected AbstractViewport mViewport;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Abstract fragment : Creating view...");

        // Load the world map from Main Activity
        Bundle args = getArguments();
        mMap = (WorldMap) args.getSerializable(MainActivity.TAG_WORLDMAP);

        // Get the Mac Address
        mMacAddress = getMacAddress(getActivity());

        // Some preferences : server IP and "using server" boolean
        mServerIP = getActivity().getPreferences(MainActivity.PREFERENCE_MODE_PRIVATE).getString(
                MainActivity.TAG_PREF_SERVER_IP,
                getResources().getString(R.string.server_ip) // default value
        );
        mUsingServer = getActivity().getPreferences(MainActivity.PREFERENCE_MODE_PRIVATE).getBoolean(
                MainActivity.TAG_PREF_USING_SERVER,
                getResources().getBoolean(R.bool.using_server) // default value
        );

        System.out.println("Abstract fragment : View created !");
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

