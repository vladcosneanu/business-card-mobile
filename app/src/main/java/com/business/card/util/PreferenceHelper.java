package com.business.card.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.business.card.R;
import com.business.card.objects.User;

public class PreferenceHelper {

    public static final String BUSINESS_CARD = "BUSINESS_CARD";

    public static final String USER_ID = "USER_ID";
    public static final String USER_TITLE = "USER_TITLE";
    public static final String USER_FIRST_NAME = "USER_FIRST_NAME";
    public static final String USER_LAST_NAME = "USER_LAST_NAME";
    public static final String USER_USERNAME = "USER_USERNAME";
    public static final String USER_PASSWORD = "USER_PASSWORD";

    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";

    /**
     * Save a key-value pair in preferences
     */
    public static void saveValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Load a key-value pair from preferences
     */
    public static String loadValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARD, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * Save the user in preferences
     */
    public static void saveUser(User user, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, user.getId());
        editor.putString(USER_FIRST_NAME, user.getFirstName());
        editor.putString(USER_LAST_NAME, user.getLastName());
        editor.putString(USER_USERNAME, user.getUsername());
        editor.putString(USER_PASSWORD, user.getPassword());
        editor.commit();
    }

    /**
     * Load the user from preferences, if the user was previously saved
     */
    public static User loadUser(Context context) {
        User user = new User();

        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARD, Context.MODE_PRIVATE);
        user.setId(sharedPreferences.getString(USER_ID, ""));
        user.setFirstName(sharedPreferences.getString(USER_FIRST_NAME, ""));
        user.setLastName(sharedPreferences.getString(USER_LAST_NAME, ""));
        user.setUsername(sharedPreferences.getString(USER_USERNAME, ""));
        user.setPassword(sharedPreferences.getString(USER_PASSWORD, ""));

        return user;
    }

    /**
     * Check if the user is logged in
     */
    public static boolean isUserLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARD, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(USER_ID, "");

        if (userId.equals("")) {
            return false;
        }

        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public static String getRegistrationId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARD, Context.MODE_PRIVATE);
        String registrationId = sharedPreferences.getString(PreferenceHelper.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("MainActivity", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = sharedPreferences.getInt(PreferenceHelper.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = Util.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("MainActivity", "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Stores the registration ID and app versionCode in the application's SharedPreferences
     *
     * @param context application's context.
     * @param regId registration ID
     */
    public static void storeRegistrationId(Context context, String regId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARD, Context.MODE_PRIVATE);
        int appVersion = Util.getAppVersion(context);
        Log.i("PreferenceHelper", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Delete all the preferences
     */
    public static void clearPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Delete all the default preferences
     */
    public static void clearDefaultPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Get the Nearby Search Radius that was set in preferences
     */
    public static String getNearbyRadius(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String nearby = sharedPreferences.getString("nearby", "100");

        return nearby;
    }
}
