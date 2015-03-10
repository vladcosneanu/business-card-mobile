package com.business.card;

import android.app.Application;

import com.business.card.objects.User;
import com.business.card.util.PreferenceHelper;

public class BusinessCardApplication extends Application {

    private static BusinessCardApplication singleton;
    public static User loggedUser;
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
    }
}
