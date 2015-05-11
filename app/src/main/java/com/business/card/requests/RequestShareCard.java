package com.business.card.requests;

import android.os.AsyncTask;
import android.util.Log;

import com.business.card.BusinessCardApplication;
import com.business.card.activities.EventCardsActivity;
import com.business.card.activities.MainActivity;
import com.business.card.objects.BusinessCard;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

public class RequestShareCard extends AsyncTask<String, Integer, JSONObject> {

    private boolean done = false;
    private MainActivity activity;
    private BusinessCard businessCard;
    private String userId;

    public RequestShareCard(MainActivity activity, BusinessCard businessCard, String userId) {
        this.activity = activity;
        this.businessCard = businessCard;
        this.userId = userId;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        byte[] result = null;
        JSONObject json = null;

        try {
            String url = "http://businesscard.netne.net/api/add/share_card.php";
            url += "?user_id=" + userId;
            url += "&card_id=" + businessCard.getId();

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
            activity.onShareCardRequestFinished(json);
        }
    }
}
