package com.business.card.requests;

import android.os.AsyncTask;
import android.util.Log;

import com.business.card.activities.NearbyCardsActivity;
import com.business.card.objects.Coordinate;
import com.business.card.objects.User;
import com.business.card.util.Util;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

public class RequestNearbyCards extends AsyncTask<String, Integer, JSONArray> {

    private boolean done = false;
    private NearbyCardsActivity activity;
    private User user;
    private Coordinate coordinate;
    private String distance;

    public RequestNearbyCards(NearbyCardsActivity activity, User user, Coordinate coordinate, String distance) {
        this.activity = activity;
        this.user = user;
        this.coordinate = coordinate;
        this.distance = distance;
    }

    /**
     * This method is executed in a background thread
     */
    @Override
    protected JSONArray doInBackground(String... params) {
        byte[] result = null;
        JSONArray json = null;

        try {
            String url = Util.HOST + "/api/get/nearby_cards.php";
            url += "?user_id=" + user.getId();
            url += "&distance=" + distance;
            url += "&lat=" + coordinate.getLatitude();
            url += "&lng=" + coordinate.getLongitude();

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

    /**
     * This method is executed on the main UI thread
     */
    @Override
    protected void onPostExecute(JSONArray json) {
        super.onPostExecute(json);

        if (done) {
            activity.onNearbyCardsRequestFinished(json);
        }
    }
}
