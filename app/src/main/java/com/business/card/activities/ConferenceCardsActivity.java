package com.business.card.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.business.card.adapters.ConferenceBusinessCardAdapter;
import com.business.card.objects.BusinessCard;
import com.business.card.objects.Conference;
import com.business.card.requests.RequestConferenceCards;
import com.business.card.requests.RequestSaveConferenceCard;
import com.business.card.util.PreferenceHelper;
import com.business.card.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ConferenceCardsActivity extends ActionBarActivity {

    private Conference conference;
    private List<BusinessCard> conferenceCards;
    private ListView conferenceCardsListView;
    private ConferenceBusinessCardAdapter adapter;
    private ProgressBar progressBar;
    private TextView noCardsAvailable;
    private BusinessCard selectedCard;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conference_cards);

        conference = BusinessCardApplication.selectedConference;

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(conference.getName());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        conferenceCardsListView = (ListView) findViewById(R.id.conference_cards_listview);
        conferenceCardsListView.setVisibility(View.GONE);

        noCardsAvailable = (TextView) findViewById(R.id.no_cards_available);
        noCardsAvailable.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RequestConferenceCards requestNearbyCards = new RequestConferenceCards(this, BusinessCardApplication.loggedUser, conference);
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
     * Finished request for conference Cards
     */
    public void onConferenceCardsRequestFinished(JSONArray j) {
        conferenceCards = new ArrayList<BusinessCard>();
        List<BusinessCard> businessCards = new ArrayList<BusinessCard>();
        for (int i = 0; i < j.length(); i++) {
            try {
                BusinessCard businessCard = BusinessCard.parseBusinessCardFromJson(j.getJSONObject(i));
                businessCards.add(businessCard);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        conferenceCards = businessCards;
        progressBar.setVisibility(View.GONE);

        if (conferenceCards.size() > 0) {
            conferenceCardsListView.setVisibility(View.VISIBLE);
            noCardsAvailable.setVisibility(View.GONE);
            adapter = new ConferenceBusinessCardAdapter(this, conferenceCards);
            conferenceCardsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            conferenceCardsListView.setVisibility(View.GONE);
            noCardsAvailable.setVisibility(View.VISIBLE);
        }
    }

    public void requestConferenceCard(BusinessCard businessCard) {
        progressDialog.show();
        selectedCard = businessCard;

        RequestSaveConferenceCard requestSaveConferenceCard = new RequestSaveConferenceCard(this, businessCard);
        requestSaveConferenceCard.execute(new String[]{});
    }

    /**
     * Finished request for getting a conference card
     */
    public void onSaveConferenceCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // card edited
                Toast.makeText(this, getString(R.string.public_card_received,
                        selectedCard.getFirstName(), selectedCard.getLastName(),
                        selectedCard.getTitle()), Toast.LENGTH_SHORT).show();

                RequestConferenceCards requestNearbyCards = new RequestConferenceCards(this, BusinessCardApplication.loggedUser, conference);
                requestNearbyCards.execute(new String[]{});

                conferenceCards.remove(selectedCard);
                if (conferenceCards.size() > 0) {
                    conferenceCardsListView.setVisibility(View.VISIBLE);
                    noCardsAvailable.setVisibility(View.GONE);
                    adapter = new ConferenceBusinessCardAdapter(this, conferenceCards);
                    conferenceCardsListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    conferenceCardsListView.setVisibility(View.GONE);
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
