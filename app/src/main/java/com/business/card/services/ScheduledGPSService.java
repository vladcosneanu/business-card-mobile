package com.business.card.services;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.business.card.R;
import com.business.card.activities.AddEditCardActivity;
import com.business.card.objects.Coordinate;
import com.business.card.objects.User;
import com.business.card.requests.RequestUpdateLocation;
import com.business.card.util.PreferenceHelper;
import com.business.card.util.Util;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScheduledGPSService extends GPSIntentService {
    public ScheduledGPSService() {
        super("ScheduledGPSService");
    }

    @Override
    protected void sendGPSCoordinates(Intent intent) {
        // check if internet is available and if the user is logged in
        if (Util.isNetworkAvailable(getApplicationContext()) && PreferenceHelper.isUserLoggedIn(getApplicationContext())) {
            Log.d(getClass().getSimpleName(), "User logged in, attempting to send location update");

            // load the logged in user from preferences
            User user = PreferenceHelper.loadUser(getApplicationContext());

            // get the latest GPS location
            LocationInfo locationInfo = new LocationInfo(getApplicationContext());
            Util.updateCoordinate(locationInfo);
            Coordinate coordinate = Util.getLocation();
            if (user != null && coordinate != null) {
                Log.d(getClass().getSimpleName(), "Sending GPS Coordinates");

                // start a request to update the user's location
                RequestUpdateLocation requestUpdateLocation = new RequestUpdateLocation(this, user, coordinate);
                requestUpdateLocation.execute(new String[]{});
            }
        } else {
            Log.d(getClass().getSimpleName(), "No internet connection or user is not logged in");
        }
    }

    /**
     * The user location update request finished
     */
    public void onLocationUpdateRequestFinished(JSONObject json) {
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // location updated
                Log.d(getClass().getSimpleName(), "User location updated successfully");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
