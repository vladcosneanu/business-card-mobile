package com.business.card.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

public class LocationBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// extract the location info in the broadcast
		final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
		// refresh the display with it
		if (locationInfo != null && locationInfo.anyLocationDataReceived()) {
			Util.updateCoordinate(locationInfo);
		}
	}
}
