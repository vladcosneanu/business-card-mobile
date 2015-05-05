package com.business.card.activities;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.fragments.EventsFragment;
import com.business.card.fragments.MyCardsFragment;
import com.business.card.fragments.SavedCardsFragment;
import com.business.card.objects.BusinessCard;
import com.business.card.objects.Event;
import com.business.card.receivers.BootCompletedReceiver;
import com.business.card.receivers.LocationBroadcastReceiver;
import com.business.card.requests.RequestAcceptPrivateEventCard;
import com.business.card.requests.RequestDenyPrivateEventCard;
import com.business.card.requests.RequestGCMRegistration;
import com.business.card.requests.RequestJoinEvent;
import com.business.card.requests.RequestMyCards;
import com.business.card.requests.RequestMyEvents;
import com.business.card.requests.RequestSavedCards;
import com.business.card.requests.RequestUpdateGCMId;
import com.business.card.services.GcmIntentService;
import com.business.card.util.PreferenceHelper;
import com.business.card.util.Util;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ViewPager pager;
    private MyPagerAdapter pagerAdapter;

    private MenuItem addMenuItem;
    private boolean displayAddMenuItem = false;
    private MenuItem searchMenuItem;
    private boolean displaySearchMenuItem = true;
    private MenuItem joinMenuItem;
    private boolean displayJoinMenuItem = false;

    private List<BusinessCard> savedCards;
    private List<BusinessCard> myCards;
    private List<Event> myEvents;

    private int currentPage;
    private ProgressDialog progressDialog;

    private LocationBroadcastReceiver lftBroadcastReceiver;
    private GoogleCloudMessaging gcm;
    private String regid;

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
            if (position == 0) {
                if (searchMenuItem != null) {
                    searchMenuItem.setVisible(true);
                } else {
                    displaySearchMenuItem = true;
                }

                if (addMenuItem != null) {
                    addMenuItem.setVisible(false);
                } else {
                    displayAddMenuItem = false;
                }

                if (joinMenuItem != null) {
                    joinMenuItem.setVisible(false);
                } else {
                    displayJoinMenuItem = false;
                }
            } else if (position == 1) {
                if (searchMenuItem != null) {
                    searchMenuItem.setVisible(false);
                } else {
                    displaySearchMenuItem = false;
                }

                if (addMenuItem != null) {
                    addMenuItem.setVisible(true);
                } else {
                    displayAddMenuItem = true;
                }

                if (joinMenuItem != null) {
                    joinMenuItem.setVisible(false);
                } else {
                    displayJoinMenuItem = false;
                }
            } else if (position == 2) {
                if (searchMenuItem != null) {
                    searchMenuItem.setVisible(false);
                } else {
                    displaySearchMenuItem = false;
                }

                if (addMenuItem != null) {
                    addMenuItem.setVisible(true);
                } else {
                    displayAddMenuItem = true;
                }

                if (joinMenuItem != null) {
                    joinMenuItem.setVisible(true);
                } else {
                    displayJoinMenuItem = true;
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setOnPageChangeListener(pageChangeListener);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (Util.checkPlayServices(this)) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = PreferenceHelper.getRegistrationId(this);

            if (regid.isEmpty()) {
                RequestGCMRegistration requestGCMRegistration = new RequestGCMRegistration(this, gcm);
                requestGCMRegistration.execute();
            } else {
                RequestUpdateGCMId requestUpdateGCMId = new RequestUpdateGCMId(this, BusinessCardApplication.loggedUser);
                requestUpdateGCMId.execute(new String[]{});
            }
        } else {
            Log.i("MainActivity", "No valid Google Play Services APK found.");
        }

        verifyReceivedIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        savedCards = (List<BusinessCard>) Util.loadList(Util.SAVED_CARDS_FILE);
        if (savedCards != null && savedCards.size() > 0) {
            // retrieved saved cards from cache file
            ((SavedCardsFragment) pagerAdapter.getItem(0)).setSavedCards(savedCards);
        }

        myCards = (List<BusinessCard>) Util.loadList(Util.MY_CARDS_FILE);
        if (myCards != null && myCards.size() > 0) {
            // retrieved my cards from cache file
            ((MyCardsFragment) pagerAdapter.getItem(1)).setMyCards(myCards);
        }

        myEvents = (List<Event>) Util.loadList(Util.EVENTS_FILE);
        if (myEvents != null && myEvents.size() > 0) {
            // retrieved events from cache file
            ((EventsFragment) pagerAdapter.getItem(2)).setMyEvents(myEvents);
        }

        RequestSavedCards requestSavedCards = new RequestSavedCards(this, BusinessCardApplication.loggedUser);
        requestSavedCards.execute(new String[]{});

        RequestMyCards requestMyCards = new RequestMyCards(this, BusinessCardApplication.loggedUser);
        requestMyCards.execute(new String[]{});

        RequestMyEvents requestMyEvents = new RequestMyEvents(this, BusinessCardApplication.loggedUser);
        requestMyEvents.execute(new String[]{});

        final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
        lftBroadcastReceiver = new LocationBroadcastReceiver();
        registerReceiver(lftBroadcastReceiver, lftIntentFilter);

        // force a location update
        LocationLibrary.forceLocationUpdate(this);

        // schedule alarm for updating the GPS location
        BootCompletedReceiver.scheduleAlarms(this);
        Log.d("GPS", "Scheduled GPS location update");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        verifyReceivedIntent(intent);

        super.onNewIntent(intent);
    }

    private void verifyReceivedIntent(Intent intent) {
        Bundle receivedBundle = intent.getExtras();
        if (receivedBundle != null && getIntent().getExtras().containsKey(Util.REQUEST_CARD_RESPONSE_EXTRA)) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(GcmIntentService.REQUEST_CARD_NOTIFICATION_ID);

            String cardId = getIntent().getExtras().getString(Util.REQUEST_CARD_RESPONSE_CARD_ID_EXTRA);
            String userId = getIntent().getExtras().getString(Util.REQUEST_CARD_RESPONSE_USER_ID_EXTRA);

            String requestCardExtra = getIntent().getExtras().getString(Util.REQUEST_CARD_RESPONSE_EXTRA);
            if (requestCardExtra.equals(Util.REQUEST_CARD_RESPONSE_ACCEPT)) {
                // Accept the card sharing request
                displayProgressDialog();
                RequestAcceptPrivateEventCard requestAcceptPrivateEventCard = new RequestAcceptPrivateEventCard(this, cardId, userId);
                requestAcceptPrivateEventCard.execute(new String[]{});
            } else if (requestCardExtra.equals(Util.REQUEST_CARD_RESPONSE_DENY)) {
                // Deny the card sharing request
                displayProgressDialog();
                RequestDenyPrivateEventCard requestDenyPrivateEventCard = new RequestDenyPrivateEventCard(this, cardId, userId);
                requestDenyPrivateEventCard.execute(new String[]{});
            }
        }
    }

    public void displayProgressDialog() {
        progressDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchMenuItem = menu.getItem(0);
        if (displaySearchMenuItem) {
            searchMenuItem.setVisible(true);
        } else {
            searchMenuItem.setVisible(false);
        }

        addMenuItem = menu.getItem(2);
        if (displayAddMenuItem) {
            addMenuItem.setVisible(true);
        } else {
            addMenuItem.setVisible(false);
        }

        joinMenuItem = menu.getItem(1);
        if (displayJoinMenuItem) {
            joinMenuItem.setVisible(true);
        } else {
            joinMenuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                // start searching for nearby public cards
                Intent nearbyIntent = new Intent(this, NearbyCardsActivity.class);
                startActivity(nearbyIntent);
                break;
            case R.id.action_add:
                if (currentPage == 1) {
                    // Clear any Business Card selected
                    BusinessCardApplication.selectedBusinessCard = null;
                    // start the edit card activity
                    Intent addEditCardIntent = new Intent(this, AddEditCardActivity.class);
                    startActivity(addEditCardIntent);
                } else if (currentPage == 2) {
                    // add a Event
                }
                break;
            case R.id.action_join:
                // Join a Event
                displayJoinEventDialog();
                break;
            case R.id.action_logout:
                (new Util()).displayConfirmLogoutDialog(this);

                return true;
            case R.id.action_settings:
                // start the settings activity
                Intent settingsINtent = new Intent(this, SettingsActivity.class);
                startActivity(settingsINtent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (lftBroadcastReceiver != null) {
            unregisterReceiver(lftBroadcastReceiver);
        }
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    public List<BusinessCard> getSavedCards() {
        return savedCards;
    }

    public List<BusinessCard> getMyCards() {
        return myCards;
    }

    public List<Event> getEvents() {
        return myEvents;
    }

    /**
     * Finished request for Saved Cards
     */
    public void onSavedCardsRequestFinished(JSONArray j) {
        savedCards = new ArrayList<BusinessCard>();
        List<BusinessCard> businessCards = new ArrayList<BusinessCard>();
        for (int i = 0; i < j.length(); i++) {
            try {
                BusinessCard businessCard = BusinessCard.parseBusinessCardFromJson(j.getJSONObject(i));
                businessCards.add(businessCard);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        savedCards = businessCards;

        // save the list to cache file
        Util.saveList(savedCards, Util.SAVED_CARDS_FILE);

        ((SavedCardsFragment) pagerAdapter.getItem(0)).setSavedCards(businessCards);
    }

    /**
     * Finished request for My Cards
     */
    public void onMyCardsRequestFinished(JSONArray j) {
        myCards = new ArrayList<BusinessCard>();
        List<BusinessCard> businessCards = new ArrayList<BusinessCard>();
        for (int i = 0; i < j.length(); i++) {
            try {
                BusinessCard businessCard = BusinessCard.parseBusinessCardFromJson(j.getJSONObject(i));
                businessCards.add(businessCard);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        myCards = businessCards;

        // save the list to cache file
        Util.saveList(myCards, Util.MY_CARDS_FILE);

        ((MyCardsFragment) pagerAdapter.getItem(1)).setMyCards(businessCards);
    }

    /**
     * Finished request for Events
     */
    public void onMyEventsRequestFinished(JSONArray j) {
        myEvents = new ArrayList<Event>();
        List<Event> events = new ArrayList<Event>();
        for (int i = 0; i < j.length(); i++) {
            try {
                Event event = Event.parseEventFromJson(j.getJSONObject(i));
                events.add(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        myEvents = events;

        // save the list to cache file
        Util.saveList(myEvents, Util.EVENTS_FILE);

        ((EventsFragment) pagerAdapter.getItem(2)).setMyEvents(myEvents);
    }

    public void onGCMRegistrationComplete(String msg) {
        Log.d("MainActivity", "msg: " + msg);
        RequestUpdateGCMId requestUpdateGCMId = new RequestUpdateGCMId(this, BusinessCardApplication.loggedUser);
        requestUpdateGCMId.execute(new String[]{});
    }

    /**
     * The GCM device registration server update request finished
     */
    public void onGCMRegistrationIdSent(JSONObject json) {
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // location updated
                Log.d(getClass().getSimpleName(), "Device GCM registration id sent to server");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finished request for My Card Delete
     */
    public void onMyCardDeleteRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // card deleted
                Toast.makeText(this, getString(R.string.card_delete_success), Toast.LENGTH_SHORT).show();

                ((MyCardsFragment) pagerAdapter.getItem(1)).removeSelectedBusinessCard();
            } else {
                // card not deleted
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Finished request for Saved Card remove
     */
    public void onSavedCardDeleteRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // card removed
                Toast.makeText(this, getString(R.string.saved_card_remove_success), Toast.LENGTH_SHORT).show();

                ((SavedCardsFragment) pagerAdapter.getItem(0)).removeSelectedBusinessCard();
            } else {
                // card not removed
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Finished request for joined Event remove
     */
    public void onJoinedEventDeleteRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // event removed
                Toast.makeText(this, getString(R.string.event_remove_success), Toast.LENGTH_SHORT).show();

                ((EventsFragment) pagerAdapter.getItem(2)).removeSelectedEvent();
            } else {
                // event not deleted
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Finished accepting the request for a private event card
     */
    public void onAcceptPrivateEventCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            int success = json.getInt("success");
            if (success == 1) {
                // card requested
                Toast.makeText(this, getString(R.string.accepted_private_card_request), Toast.LENGTH_LONG).show();
            } else {
                // card not requested
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finished denying the request for a private event card
     */
    public void onDenyPrivateEventCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            int success = json.getInt("success");
            if (success == 1) {
                // card requested
                Toast.makeText(this, getString(R.string.denied_private_card_request), Toast.LENGTH_LONG).show();
            } else {
                // card not requested
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayJoinEventDialog() {
        View dialoglayout = getLayoutInflater().inflate(R.layout.alert_edit, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.event_passcode_title));
        builder.setMessage(getString(R.string.event_passcode_message));

        final EditText textEntryView = (EditText) dialoglayout.findViewById(R.id.value);

        builder.setPositiveButton(getResources().getString(R.string.submit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String value = textEntryView.getText().toString().trim();
                if (value.equals("")) {
                    Toast.makeText(MainActivity.this, R.string.invalid_passcode, Toast.LENGTH_SHORT).show();
                    return;
                } else if (value.length() < 4) {
                    Toast.makeText(MainActivity.this, R.string.invalid_passcode, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // the Event Passcode can be submitted
                    progressDialog.show();
                    RequestJoinEvent requestJoinEvent = new RequestJoinEvent(MainActivity.this, value);
                    requestJoinEvent.execute(new String[]{});
                }
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.cancel), null);

        builder.setView(dialoglayout);
        builder.show();
    }

    /**
     * Finished joining a event
     */
    public void onJoinEventRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // Event joined
                String eventName = json.getString("event");

                Toast.makeText(this, getString(R.string.event_joined, eventName), Toast.LENGTH_SHORT).show();

                RequestMyEvents requestMyEvents = new RequestMyEvents(this, BusinessCardApplication.loggedUser);
                requestMyEvents.execute(new String[]{});
            } else if (success.equals("false")) {
                // Event not joined
                String error = json.getString("error");
                if (error.equals("Event does not exist")) {
                    Toast.makeText(this, getString(R.string.passcode_does_not_exist), Toast.LENGTH_SHORT).show();
                } else if (error.equals("User already added to event")) {
                    Toast.makeText(this, getString(R.string.event_already_joined), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private SavedCardsFragment savedCardsFragment;
        private MyCardsFragment myCardsFragment;
        private EventsFragment eventsFragment;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    if (savedCardsFragment == null) {
                        savedCardsFragment = new SavedCardsFragment();
                    }
                    return savedCardsFragment;
                case 1:
                    if (myCardsFragment == null) {
                        myCardsFragment = new MyCardsFragment();
                    }
                    return myCardsFragment;
                case 2:
                    if (eventsFragment == null) {
                        eventsFragment = new EventsFragment();
                    }
                    return eventsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.saved_cards);
                case 1:
                    return getString(R.string.my_cards);
                case 2:
                    return getString(R.string.events);
                default:
                    return null;
            }
        }
    }
}