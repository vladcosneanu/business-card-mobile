package com.business.card.requests;

import android.os.AsyncTask;
import android.util.Log;

import com.business.card.BusinessCardApplication;
import com.business.card.activities.MainActivity;
import com.business.card.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

public class RequestJoinEvent extends AsyncTask<String, Integer, JSONObject> {

    private boolean done = false;
    private MainActivity activity;
    private String passcode;

    public RequestJoinEvent(MainActivity activity, String passcode) {
        this.activity = activity;
        this.passcode = passcode;
    }

    /**
     * This method is executed in a background thread
     */
    @Override
    protected JSONObject doInBackground(String... params) {
        byte[] result = null;
        JSONObject json = null;

        try {
            String url = Util.HOST + "/api/add/join_event.php";
            url += "?user_id=" + BusinessCardApplication.loggedUser.getId();
            url += "&passcode=" + passcode;

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

    /**
     * This method is executed on the main UI thread
     */
    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);

        if (done) {
            activity.onJoinEventRequestFinished(json);
        }
    }
}
