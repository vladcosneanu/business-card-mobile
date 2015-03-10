package com.business.card.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.business.card.R;
import com.business.card.objects.User;

public class PreferenceHelper {

    public static final String BUSINESS_CARE = "BUSINESS_CARE";

    public static final String USER_ID = "USER_ID";
    public static final String USER_TITLE = "USER_TITLE";
    public static final String USER_FIRST_NAME = "USER_FIRST_NAME";
    public static final String USER_LAST_NAME = "USER_LAST_NAME";
    public static final String USER_USERNAME = "USER_USERNAME";
    public static final String USER_PASSWORD = "USER_PASSWORD";

    public static void saveValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String loadValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void saveUser(User user, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, user.getId());
        editor.putString(USER_TITLE, user.getTitle());
        editor.putString(USER_FIRST_NAME, user.getFirstName());
        editor.putString(USER_LAST_NAME, user.getLastName());
        editor.putString(USER_USERNAME, user.getUsername());
        editor.putString(USER_PASSWORD, user.getPassword());
        editor.commit();
    }

    public static User loadUser(Context context) {
        User user = new User();

        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARE, Context.MODE_PRIVATE);
        user.setId(sharedPreferences.getString(USER_ID, ""));
        user.setTitle(sharedPreferences.getString(USER_TITLE, ""));
        user.setFirstName(sharedPreferences.getString(USER_FIRST_NAME, ""));
        user.setLastName(sharedPreferences.getString(USER_LAST_NAME, ""));
        user.setUsername(sharedPreferences.getString(USER_USERNAME, ""));
        user.setPassword(sharedPreferences.getString(USER_PASSWORD, ""));

        return user;
    }

    public static boolean isUserLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARE, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(USER_ID, "");

        if (userId.equals("")) {
            return false;
        }

        return true;
    }

    public static void deletSavedUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BUSINESS_CARE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_ID);
        editor.remove(USER_TITLE);
        editor.remove(USER_FIRST_NAME);
        editor.remove(USER_LAST_NAME);
        editor.remove(USER_USERNAME);
        editor.remove(USER_PASSWORD);
        editor.commit();

        Toast.makeText(context, context.getString(R.string.logout_successful), Toast.LENGTH_SHORT).show();
    }
}
