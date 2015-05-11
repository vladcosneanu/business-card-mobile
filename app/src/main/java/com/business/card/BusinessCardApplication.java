package com.business.card;

import android.app.Application;
import android.util.Log;

import com.business.card.objects.BusinessCard;
import com.business.card.objects.Event;
import com.business.card.objects.User;
import com.business.card.util.PreferenceHelper;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

public class BusinessCardApplication extends Application {

    private static BusinessCardApplication singleton;
    public static User loggedUser;
    public static User selectedUser;
    public static BusinessCard selectedBusinessCard;
    public static Event selectedEvent;
    public String generatedUsername = "";
    public String generatedPassword = "";

    public static BusinessCardApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;
        if (PreferenceHelper.isUserLoggedIn(this)) {
            loggedUser = PreferenceHelper.loadUser(this);
        }

        try {
            LocationLibrary.initialiseLibrary(getBaseContext(), "com.business.card");
            LocationLibrary.useFineAccuracyForRequests(true);
        } catch (Exception e) {
            Log.e("BusinessCardApplication", "Could not setup the location library: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
