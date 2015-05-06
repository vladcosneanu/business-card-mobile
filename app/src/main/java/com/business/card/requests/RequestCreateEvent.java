package com.business.card.requests;

import android.os.AsyncTask;
import android.util.Log;

import com.business.card.BusinessCardApplication;
import com.business.card.activities.CreateEventActivity;
import com.business.card.objects.Event;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class RequestCreateEvent extends AsyncTask<String, Integer, JSONObject> {

    private boolean done = false;
    private CreateEventActivity activity;
    private Event event;

    public RequestCreateEvent(CreateEventActivity activity, Event event) {
        this.activity = activity;
        this.event = event;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        byte[] result = null;
        JSONObject json = null;

        try {
            String url = "http://businesscard.netne.net/api/add/event.php";
            url += "?user_id=" + BusinessCardApplication.loggedUser.getId();
            url += "&name=" + URLEncoder.encode(event.getName(), "UTF-8");
            url += "&location=" + URLEncoder.encode(event.getLocation(), "UTF-8");
            url += "&date=" + URLEncoder.encode(event.getDate(), "UTF-8");
            url += "&passcode=" + URLEncoder.encode(event.getPasscode(), "UTF-8");

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
            activity.onCreateEventRequestFinished(json);
        }
    }
}
