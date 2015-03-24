package com.business.card.requests;

import android.os.AsyncTask;
import android.util.Log;

import com.business.card.activities.MainActivity;
import com.business.card.objects.BusinessCard;
import com.business.card.objects.User;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

public class RequestSavedCards extends AsyncTask<String, Integer, JSONArray> {

    private boolean done = false;
    private MainActivity activity;
    private User user;
    private BusinessCard businessCard;

    public RequestSavedCards(MainActivity activity, User user) {
        this.activity = activity;
        this.user = user;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        byte[] result = null;
        JSONArray json = null;

        try {
            String url = "http://businesscard.netne.net/api/get/saved_cards.php";
            url += "?user_id=" + user.getId();

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
                json = new JSONArray(str);
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
    protected void onPostExecute(JSONArray json) {
        super.onPostExecute(json);

        if (done) {
            activity.onSavedCardsRequestFinished(json);
        }
    }
}
