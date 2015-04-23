package com.business.card.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.adapters.NearbyBusinessCardAdapter;
import com.business.card.objects.BusinessCard;
import com.business.card.requests.RequestGetPublicCard;
import com.business.card.requests.RequestNearbyCards;
import com.business.card.util.PreferenceHelper;
import com.business.card.util.Util;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NearbyCardsActivity extends ActionBarActivity {

    private List<BusinessCard> nearbyCards;
    private ListView nearbyCardsListView;
    private NearbyBusinessCardAdapter adapter;
    private ProgressBar progressBar;
    private TextView noCardsAvailable;
    private BusinessCard selectedCard;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_cards);

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        nearbyCardsListView = (ListView) findViewById(R.id.nearby_cards_listview);
        nearbyCardsListView.setVisibility(View.GONE);

        noCardsAvailable = (TextView) findViewById(R.id.no_cards_available);
        noCardsAvailable.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocationInfo latestInfo = new LocationInfo(getBaseContext());
        Util.updateCoordinate(latestInfo);

        RequestNearbyCards requestNearbyCards = new RequestNearbyCards(this, BusinessCardApplication.loggedUser, Util
                .getLocation(), 1000);
        requestNearbyCards.execute(new String[]{});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nearby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // the top left back button was clicked
                finish();
                break;
            case R.id.action_logout:
                Util.displayConfirmLogoutDialog(this);

                return true;
            default:
                break;
        }

        return true;
    }

    /**
     * Finished request for nearby Cards
     */
    public void onNearbyCardsRequestFinished(JSONArray j) {
        nearbyCards = new ArrayList<BusinessCard>();
        List<BusinessCard> businessCards = new ArrayList<BusinessCard>();
        for (int i = 0; i < j.length(); i++) {
            try {
                BusinessCard businessCard = BusinessCard.parseBusinessCardFromJson(j.getJSONObject(i));
                businessCards.add(businessCard);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        nearbyCards = businessCards;
        progressBar.setVisibility(View.GONE);

        if (nearbyCards.size() > 0) {
            nearbyCardsListView.setVisibility(View.VISIBLE);
            noCardsAvailable.setVisibility(View.GONE);
            adapter = new NearbyBusinessCardAdapter(this, nearbyCards);
            nearbyCardsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            nearbyCardsListView.setVisibility(View.GONE);
            noCardsAvailable.setVisibility(View.VISIBLE);
        }
    }

    public void requestPublicCard(BusinessCard businessCard) {
        progressDialog.show();
        selectedCard = businessCard;

        RequestGetPublicCard requestGetPublicCard = new RequestGetPublicCard(this, businessCard);
        requestGetPublicCard.execute(new String[]{});
    }

    /**
     * Finished request for getting a public card
     */
    public void onGetPublicCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // card edited
                Toast.makeText(this, getString(R.string.public_card_received,
                        selectedCard.getFirstName(), selectedCard.getLastName(),
                        selectedCard.getTitle()), Toast.LENGTH_SHORT).show();

                nearbyCards.remove(selectedCard);
                if (nearbyCards.size() > 0) {
                    nearbyCardsListView.setVisibility(View.VISIBLE);
                    noCardsAvailable.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                } else {
                    nearbyCardsListView.setVisibility(View.GONE);
                    noCardsAvailable.setVisibility(View.VISIBLE);
                }
            } else {
                // card not edited
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
