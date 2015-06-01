package fr.gi.utbm.lo53project;

import java.net.URL;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by celian on 05/05/15 for LO53Project
 */
public abstract class AbstractFragment extends Fragment
{

	protected WorldMap	mMap;
	protected URL		mUrl;

	@Nullable
	@Override
	public View onCreateView(
			final LayoutInflater inflater,
			final ViewGroup container,
			final Bundle savedInstanceState)
	{

		// Load the world map from Main Activity
		Bundle args = getArguments();
		mMap = (WorldMap) args.getSerializable(MainActivity.TAG_WORLDMAP);

		return null;
	}

}
