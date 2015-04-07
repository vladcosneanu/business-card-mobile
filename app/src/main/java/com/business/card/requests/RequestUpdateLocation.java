package com.business.card.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.business.card.objects.Coordinate;
import com.business.card.objects.User;
import com.business.card.services.ScheduledGPSService;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

public class RequestUpdateLocation extends AsyncTask<String, Integer, JSONObject> {

    private boolean done = false;
    private ScheduledGPSService service;
    private User user;
    private Coordinate coordinate;

    public RequestUpdateLocation(ScheduledGPSService service, User user, Coordinate coordinate) {
        this.service = service;
        this.user = user;
        this.coordinate = coordinate;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        byte[] result = null;
        JSONObject json = null;

        try {
            String url = "http://businesscard.netne.net/api/update/user_location.php";
            url += "?id=" + user.getId();
            url += "&lat=" + coordinate.getLatitude();
            url += "&lng=" + coordinate.getLongitude();
            url += "&timestamp=" + coordinate.getLocationUpdateTimestamp();

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
            service.onLocaionUpdateRequestFinished(json);
        }
    }
}
