package com.business.card.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.business.card.R;
import com.business.card.activities.NotLoggedActivity;
import com.business.card.objects.Coordinate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final int CONTEXT_MENU_ITEM_MY_CARDS_SHARE = 0;
    public static final int CONTEXT_MENU_ITEM_MY_CARDS_EDIT = 1;
    public static final int CONTEXT_MENU_ITEM_MY_CARDS_DELETE = 2;

    public static final int CONTEXT_MENU_ITEM_CONFERENCES_DELETE = 3;

    public static final int CONTEXT_MENU_ITEM_SAVED_CARDS_DELETE = 4;

    public static final String SENDER_ID = "384500724074";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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
        coordinate.setLocationUpdateTimestamp(locationInfo.lastLocationUpdateTimestamp);
        coordinate.setAccuracy(locationInfo.lastAccuracy);
    }
    
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("MainActivity", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public static void displayConfirmLogoutDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.action_logout);
        builder.setMessage(R.string.logout_message);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Yes" button, delete the selected business card
                // remove the previously saved user
                PreferenceHelper.clearPreferences(activity);

                Toast.makeText(activity, R.string.logout_successful, Toast.LENGTH_SHORT).show();

                // start the initial activity, clearing any other activities previously opened
                Intent intent = new Intent(activity, NotLoggedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.no, null);

        builder.show();
    }
}
