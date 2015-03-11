package com.business.card.requests;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.business.card.BusinessCardApplication;
import com.business.card.activities.CreateAccountActivity;
import com.business.card.objects.BusinessCard;
import com.business.card.objects.User;

public class RequestSignUp extends AsyncTask<String, Integer, JSONObject> {

    private boolean done = false;
    private CreateAccountActivity activity;
    private User user;
    private BusinessCard businessCard;

    public RequestSignUp(CreateAccountActivity activity, User user, BusinessCard businessCard) {
        this.activity = activity;
        this.user = user;
        this.businessCard = businessCard;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        byte[] result = null;
        JSONObject json = null;

        try {
            String url = "http://businesscard.netne.net/api/add/sign_up.php";
            url += "?title=" + URLEncoder.encode(user.getTitle(), "UTF-8");
            url += "&first_name=" + URLEncoder.encode(user.getFirstName(), "UTF-8");
            url += "&last_name=" + URLEncoder.encode(user.getLastName(), "UTF-8");
            url += "&email=" + URLEncoder.encode(businessCard.getEmail(), "UTF-8");
            url += "&phone=" + URLEncoder.encode(businessCard.getPhone(), "UTF-8");
            url += "&username=" + URLEncoder.encode(user.getUsername(), "UTF-8");
            url += "&password=" + URLEncoder.encode(user.getPassword(), "UTF-8");

            Log.e("request", url);
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();
            Log.d("status", "status:" + statusLine.toString());
            if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
                result = EntityUtils.toByteArray(response.getEntity());
                publishProgress(result.length);
                String str = new String(result, "UTF-8");
                json = new JSONObject(str);
            }

            if (json != null) {
                done = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d("size", values[0].toString());
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);

        if (done) {
            activity.onSignUpRequestFinished(json);
        }
    }
}
