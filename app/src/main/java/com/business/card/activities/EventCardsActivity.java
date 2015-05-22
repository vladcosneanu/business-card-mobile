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
import com.business.card.adapters.EventBusinessCardAdapter;
import com.business.card.objects.BusinessCard;
import com.business.card.objects.Event;
import com.business.card.requests.RequestEventCards;
import com.business.card.requests.RequestPrivateEventCard;
import com.business.card.requests.RequestPublicEventCard;
import com.business.card.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventCardsActivity extends ActionBarActivity {

    private Event event;
    private List<BusinessCard> eventCards;
    private ListView eventCardsListView;
    private EventBusinessCardAdapter adapter;
    private ProgressBar progressBar;
    private TextView noCardsAvailable;
    private BusinessCard selectedCard;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_cards);

        event = BusinessCardApplication.selectedEvent;

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(event.getName());

        // initialize a progress dialog that will be displayed with server requests
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        // get references to the UI elements
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        eventCardsListView = (ListView) findViewById(R.id.event_cards_listview);
        eventCardsListView.setVisibility(View.GONE);

        noCardsAvailable = (TextView) findViewById(R.id.no_cards_available);
        noCardsAvailable.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // start the request to receive the cards for the selected event
        RequestEventCards requestEventCards = new RequestEventCards(this, BusinessCardApplication.loggedUser, event);
        requestEventCards.execute(new String[]{});
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
                (new Util()).displayConfirmLogoutDialog(this);

                return true;
            case R.id.action_settings:
                // start the settings activity
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);

                break;
            default:
                break;
        }

        return true;
    }

    /**
     * Finished request for event Cards
     */
    public void onEventCardsRequestFinished(JSONArray j) {
        eventCards = new ArrayList<BusinessCard>();
        List<BusinessCard> businessCards = new ArrayList<BusinessCard>();
        for (int i = 0; i < j.length(); i++) {
            try {
                BusinessCard businessCard = BusinessCard.parseBusinessCardFromJson(j.getJSONObject(i));
                businessCards.add(businessCard);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        eventCards = businessCards;
        progressBar.setVisibility(View.GONE);

        if (eventCards.size() > 0) {
            // this event has cards to display for this user
            eventCardsListView.setVisibility(View.VISIBLE);
            noCardsAvailable.setVisibility(View.GONE);
            adapter = new EventBusinessCardAdapter(this, eventCards);
            eventCardsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            // this event doesn't have any cards to display for this user
            eventCardsListView.setVisibility(View.GONE);
            noCardsAvailable.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Request a public card
     */
    public void requestPublicEventCard(BusinessCard businessCard) {
        progressDialog.show();
        selectedCard = businessCard;

        RequestPublicEventCard requestPublicEventCard = new RequestPublicEventCard(this, businessCard);
        requestPublicEventCard.execute(new String[]{});
    }

    /**
     * Request a private card
     */
    public void requestPrivateEventCard(BusinessCard businessCard) {
        progressDialog.show();
        selectedCard = businessCard;

        RequestPrivateEventCard requestPrivateEventCard = new RequestPrivateEventCard(this, businessCard);
        requestPrivateEventCard.execute(new String[]{});
    }

    /**
     * Finished request for getting a public event card
     */
    public void onPublicEventCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // card saved
                Toast.makeText(this, getString(R.string.public_card_received,
                        selectedCard.getFirstName(), selectedCard.getLastName(),
                        selectedCard.getTitle()), Toast.LENGTH_SHORT).show();

                RequestEventCards requestEventCards = new RequestEventCards(this, BusinessCardApplication.loggedUser, event);
                requestEventCards.execute(new String[]{});

                // remove the saved card, as it was already added to Saved Cards
                eventCards.remove(selectedCard);
                if (eventCards.size() > 0) {
                    // this event has cards to display for this user
                    eventCardsListView.setVisibility(View.VISIBLE);
                    noCardsAvailable.setVisibility(View.GONE);
                    adapter = new EventBusinessCardAdapter(this, eventCards);
                    eventCardsListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    // this event doesn't have any cards to display for this user
                    eventCardsListView.setVisibility(View.GONE);
                    noCardsAvailable.setVisibility(View.VISIBLE);
                }
            } else {
                // card not saved
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finished requesting a private event card
     */
    public void onPrivateEventCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            int success = json.getInt("success");
            if (success == 1) {
                // card requested
                Toast.makeText(this, getString(R.string.private_card_requested,
                        selectedCard.getFirstName(), selectedCard.getLastName(),
                        selectedCard.getTitle()), Toast.LENGTH_LONG).show();
            } else {
                // card not requested
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
