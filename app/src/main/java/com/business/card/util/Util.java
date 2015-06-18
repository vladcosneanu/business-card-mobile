package com.business.card.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.activities.NotLoggedActivity;
import com.business.card.objects.Coordinate;
import com.business.card.requests.RequestUpdateLogout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final String HOST = "http://businesscard.net84.net";

    public static final int CONTEXT_MENU_ITEM_MY_CARDS_SHARE = 0;
    public static final int CONTEXT_MENU_ITEM_MY_CARDS_EDIT = 1;
    public static final int CONTEXT_MENU_ITEM_MY_CARDS_DELETE = 2;

    public static final int CONTEXT_MENU_ITEM_EVENTS_REMOVE = 3;

    public static final int CONTEXT_MENU_ITEM_SAVED_CARDS_REMOVE = 4;

    public static final String SENDER_ID = "384500724074";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String REQUEST_CARD_RESPONSE_EXTRA = "REQUEST_CARD_RESPONSE_EXTRA";
    public static final String REQUEST_CARD_RESPONSE_ACCEPT = "REQUEST_CARD_RESPONSE_ACCEPT";
    public static final String REQUEST_CARD_RESPONSE_DENY = "REQUEST_CARD_RESPONSE_DENY";
    public static final String REQUEST_CARD_RESPONSE_CARD_ID_EXTRA = "REQUEST_CARD_RESPONSE_CARD_ID_EXTRA";
    public static final String REQUEST_CARD_RESPONSE_USER_ID_EXTRA = "REQUEST_CARD_RESPONSE_USER_ID_EXTRA";

    public static final String SHARE_CARD_RESPONSE_EXTRA = "SHARE_CARD_RESPONSE_EXTRA";
    public static final String SHARE_CARD_RESPONSE_SAVE = "SHARE_CARD_RESPONSE_SAVE";
    public static final String SHARE_CARD_RESPONSE_CANCEL = "SHARE_CARD_RESPONSE_CANCEL";
    public static final String SHARE_CARD_RESPONSE_CARD_ID_EXTRA = "SHARE_CARD_RESPONSE_CARD_ID_EXTRA";
    public static final String SHARE_CARD_RESPONSE_USER_ID_EXTRA = "SHARE_CARD_RESPONSE_USER_ID_EXTRA";

    public static final String ACCEPT_REQUEST_CARD_ACTION = "ACCEPT_REQUEST_CARD_ACTION";
    public static final String DENY_REQUEST_CARD_ACTION = "DENY_REQUEST_CARD_ACTION";
    public static final String ACCEPT_REQUEST_CARD_GRANTED_ACTION = "ACCEPT_REQUEST_CARD_GRANTED_ACTION";

    public static final String SAVE_SHARE_CARD_ACTION = "SAVE_SHARE_CARD_ACTION";
    public static final String CANCEL_SHARE_CARD_ACTION = "CANCEL_SHARE_CARD_ACTION";

    public static final String SAVED_CARDS_FILE = "saved_cards";
    public static final String MY_CARDS_FILE = "my_cards";
    public static final String EVENTS_FILE = "events_file";

    private static Coordinate coordinate;
    private static ProgressDialog progressDialog;
    private static Activity activity;

    /**
     * This method is used to verify that a string is a valid email address
     */
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

    /**
     * This method verifies if there is an active internet connection available
     */
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

    /**
     * Display the logout confirmation dialog
     */
    public void displayConfirmLogoutDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.action_logout);
        builder.setMessage(R.string.logout_message);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (isNetworkAvailable(activity)) {
                    // User clicked "Yes" button, delete the location info and GCM data from server
                    Util.activity = activity;

                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage(activity.getString(R.string.please_wait));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(true);
                    progressDialog.show();

                    RequestUpdateLogout requestUpdateLogout = new RequestUpdateLogout(Util.this, BusinessCardApplication.loggedUser);
                    requestUpdateLogout.execute(new String[]{});
                } else {
                    displayNoNetworkForLogout(activity);
                }
            }
        });
        builder.setNegativeButton(R.string.no, null);

        builder.show();
    }

    /**
     * Display the Internet required dialog
     */
    public void displayInternetRequiredDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.internet_required);
        builder.setMessage(R.string.internet_required_message);

        builder.setPositiveButton(R.string.ok, null);

        builder.show();
    }

    /**
     * Display the Internet required dialog, with a custom message
     */
    public void displayInternetRequiredCustomDialog(final Activity activity, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.internet_required);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.ok, null);

        builder.show();
    }

    /**
     * Logout request finished
     */
    public void onLogoutFinished(JSONObject json) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // logout finished
                PreferenceHelper.clearPreferences(activity);
                PreferenceHelper.clearDefaultPreferences(activity);

                // cleared any cached files
                clearCachedFiles();

                BusinessCardApplication.selectedBusinessCard = null;
                BusinessCardApplication.selectedEvent = null;
                BusinessCardApplication.loggedUser = null;

                Toast.makeText(activity, R.string.logout_successful, Toast.LENGTH_SHORT).show();

                // start the initial activity, clearing any other activities previously opened
                Intent intent = new Intent(activity, NotLoggedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display the No Network for Logout dialog
     */
    private static void displayNoNetworkForLogout(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.logout_network_title);
        builder.setMessage(R.string.logout_network_message);
        builder.setNegativeButton(R.string.ok, null);
        builder.show();
    }

    /**
     * Get the card layout background color resource, based on layout number
     */
    public static int getColorByCardLayoutNo(int layoutNo) {
        switch (layoutNo) {
            case 1:
                return R.color.actionbat_background;
            case 2:
                return R.color.red_card;
            case 3:
                return R.color.orange_card;
            case 4:
                return R.color.green_card;
            case 5:
                return R.color.purple_card;
            case 6:
                return R.color.yellow_card;
            default:
                return R.color.actionbat_background;
        }
    }

    /**
     * Get the card layout resource, based on layout number
     */
    public static int getLayoutByCardLayoutNo(int layoutNo) {
        switch (layoutNo) {
            case 1:
                return R.layout.card_layout_1;
            case 2:
                return R.layout.card_layout_2;
            case 3:
                return R.layout.card_layout_3;
            case 4:
                return R.layout.card_layout_4;
            case 5:
                return R.layout.card_layout_5;
            case 6:
                return R.layout.card_layout_6;
            default:
                return R.layout.card_layout_1;
        }
    }

    /**
     * Save a list of serializable elements to a specific filename, for caching
     */
    public static void saveList(List<? extends Serializable> serializableList, String filename) {
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/business_card/" + filename);
            file.getParentFile().mkdirs();
            file.createNewFile();

            // write the list in the file
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(serializableList);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Load a previously saved list of serializable elements from a specific filename
     */
    public static List<? extends Serializable> loadList(String filename) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/business_card/" + filename);
        if (!file.exists()) {
            return null;
        }

        try {
            // read the list from the file
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            List<? extends Serializable> list = (List<? extends Serializable>) inputStream.readObject();
            inputStream.close();

            return list;
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Delete all cached files
     */
    public static void clearCachedFiles() {
        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/business_card/");
        String[] entries = folder.list();
        for (String s : entries) {
            File currentFile = new File(folder.getPath(), s);
            currentFile.delete();
        }
        folder.delete();
    }
}
