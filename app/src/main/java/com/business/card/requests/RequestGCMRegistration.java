package com.business.card.requests;

import android.os.AsyncTask;
import android.util.Log;

import com.business.card.activities.AddEditCardActivity;
import com.business.card.activities.MainActivity;
import com.business.card.objects.BusinessCard;
import com.business.card.util.PreferenceHelper;
import com.business.card.util.Util;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class RequestGCMRegistration extends AsyncTask<Void, Void, String> {

    private MainActivity activity;
    private GoogleCloudMessaging gcm;
    private String regid;

    public RequestGCMRegistration(MainActivity activity, GoogleCloudMessaging gcm) {
        this.activity = activity;
        this.gcm = gcm;
    }

    /**
     * This method is executed in a background thread
     */
    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(activity);
            }
            regid = gcm.register(Util.SENDER_ID);
            msg = "Device registered, registration ID = " + regid;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            //sendRegistrationIdToBackend();

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the registration ID - no need to register again.
            PreferenceHelper.storeRegistrationId(activity, regid);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
    }

    /**
     * This method is executed on the main UI thread
     */
    @Override
    protected void onPostExecute(String msg) {
        activity.onGCMRegistrationComplete(msg);
    }
}
