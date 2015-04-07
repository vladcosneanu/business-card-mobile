package com.business.card.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.business.card.util.Util;
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
