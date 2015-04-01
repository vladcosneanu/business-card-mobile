package com.business.card.util;

import android.util.Log;

import com.business.card.objects.Coordinate;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static Coordinate coordinate;

    public static boolean isEmailValid(String email) {
        String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static Coordinate getLocation() {
        if (coordinate == null) {
            coordinate = new Coordinate();
            coordinate.setValid(false);
        }
        return coordinate;
    }

    public static void updateCoordinate(LocationInfo locationInfo) {
        coordinate = new Coordinate();
        Log.i("Location", "Location update at:" + LocationInfo.formatTimeAndDay(locationInfo.lastLocationUpdateTimestamp, true)
                + ", accuracy: " + locationInfo.lastAccuracy + ", lat: " + locationInfo.lastLat + ", lng: " + locationInfo.lastLong);
        coordinate.setLatitude(locationInfo.lastLat);
        coordinate.setLongitude(locationInfo.lastLong);
        coordinate.setValid(true);
        coordinate.setAccuracy(locationInfo.lastAccuracy);
    }
}
