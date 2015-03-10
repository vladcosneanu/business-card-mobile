package com.business.card.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.business.card.R;
import com.business.card.util.PreferenceHelper;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // thread for displaying the SplashScreen
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (PreferenceHelper.isUserLoggedIn(SplashActivity.this)) {
                    // start the main activity, clearing any other activities previously opened
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    // start the main activity, clearing any other activities previously opened
                    Intent intent = new Intent(SplashActivity.this, NotLoggedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
        splashTread.start();
    }
}
