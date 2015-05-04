package com.business.card.requests;

import android.os.AsyncTask;
import android.util.Log;

import com.business.card.activities.EventCardsActivity;
import com.business.card.objects.Event;
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

public class RequestEventCards extends AsyncTask<String, Integer, JSONArray> {

    private boolean done = false;
    private EventCardsActivity activity;
    private User user;
    private Event event;

    public RequestEventCards(EventCardsActivity activity, User user, Event event) {
        this.activity = activity;
        this.user = user;
        this.event = event;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        byte[] result = null;
        JSONArray json = null;

        try {
            String url = "http://businesscard.netne.net/api/get/event_cards.php";
            url += "?user_id=" + user.getId();
            url += "&event_id=" + event.getId();

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
            activity.onEventCardsRequestFinished(json);
        }
    }
}
