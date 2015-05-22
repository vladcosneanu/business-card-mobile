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
import com.business.card.objects.User;
import com.business.card.receivers.BootCompletedReceiver;
import com.business.card.receivers.LocationBroadcastReceiver;
import com.business.card.requests.RequestAcceptPrivateEventCard;
import com.business.card.requests.RequestDenyPrivateEventCard;
import com.business.card.requests.RequestEvents;
import com.business.card.requests.RequestGCMRegistration;
import com.business.card.requests.RequestJoinEvent;
import com.business.card.requests.RequestMyCards;
import com.business.card.requests.RequestSaveSharedCard;
import com.business.card.requests.RequestSavedCards;
import com.business.card.requests.RequestShareCard;
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
    private List<Event> events;

    private int currentPage;
    private ProgressDialog progressDialog;

    private LocationBroadcastReceiver lftBroadcastReceiver;
    private GoogleCloudMessaging gcm;
    private String regid;

    // instantiate the viewpager's page change listener
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
            if (position == 0) {
                // "Saved Cards" page selected
                // display the search icon
                if (searchMenuItem != null) {
                    searchMenuItem.setVisible(true);
                } else {
                    displaySearchMenuItem = true;
                }

                // hide the "+" icon
                if (addMenuItem != null) {
                    addMenuItem.setVisible(false);
                } else {
                    displayAddMenuItem = false;
                }

                // hide the "join event" icon
                if (joinMenuItem != null) {
                    joinMenuItem.setVisible(false);
                } else {
                    displayJoinMenuItem = false;
                }
            } else if (position == 1) {
                // "My Cards" page selected
                // hide the "search" icon
                if (searchMenuItem != null) {
                    searchMenuItem.setVisible(false);
                } else {
                    displaySearchMenuItem = false;
                }

                // display the "+" icon
                if (addMenuItem != null) {
                    addMenuItem.setVisible(true);
                } else {
                    displayAddMenuItem = true;
                }

                // hide the "join event" icon
                if (joinMenuItem != null) {
                    joinMenuItem.setVisible(false);
                } else {
                    displayJoinMenuItem = false;
                }
            } else if (position == 2) {
                // "Events" page selected
                // hide the "search" icon
                if (searchMenuItem != null) {
                    searchMenuItem.setVisible(false);
                } else {
                    displaySearchMenuItem = false;
                }

                // display the "+" icon
                if (addMenuItem != null) {
                    addMenuItem.setVisible(true);
                } else {
                    displayAddMenuItem = true;
                }

                // display the "join event" icon
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

        // initialize a progress dialog that will be displayed with server requests
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        // get references to the UI elements
        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setOnPageChangeListener(pageChangeListener);
        // instantiate the pager adapter and set it to the viewpager
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (Util.checkPlayServices(this)) {
            // Google Play Services installed
            gcm = GoogleCloudMessaging.getInstance(this);
            // get the GCM reg id from preferences
            regid = PreferenceHelper.getRegistrationId(this);

            if (regid.isEmpty()) {
                // GCM reg id does not exist, request a new one
                RequestGCMRegistration requestGCMRegistration = new RequestGCMRegistration(this, gcm);
                requestGCMRegistration.execute();
            } else {
                // GCM reg id exists, update the server
                RequestUpdateGCMId requestUpdateGCMId = new RequestUpdateGCMId(this, BusinessCardApplication.loggedUser);
                requestUpdateGCMId.execute(new String[]{});
            }
        } else {
            Log.i("MainActivity", "No valid Google Play Services APK found.");
        }

        // check if a notification started this activity
        verifyReceivedIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // load the saved cards list from cache
        savedCards = (List<BusinessCard>) Util.loadList(Util.SAVED_CARDS_FILE);
        if (savedCards != null && savedCards.size() > 0) {
            // retrieved saved cards from cache file
            ((SavedCardsFragment) pagerAdapter.getItem(0)).setSavedCards(savedCards);
        }

        // load the my cards list from cache
        myCards = (List<BusinessCard>) Util.loadList(Util.MY_CARDS_FILE);
        if (myCards != null && myCards.size() > 0) {
            // retrieved my cards from cache file
            ((MyCardsFragment) pagerAdapter.getItem(1)).setMyCards(myCards);
        }

        // load the events list from cache
        events = (List<Event>) Util.loadList(Util.EVENTS_FILE);
        if (events != null && events.size() > 0) {
            // retrieved events from cache file
            ((EventsFragment) pagerAdapter.getItem(2)).setMyEvents(events);
        }

        // start requests for updating the main pages: saved cards, my cards and events
        RequestSavedCards requestSavedCards = new RequestSavedCards(this, BusinessCardApplication.loggedUser);
        requestSavedCards.execute(new String[]{});

        RequestMyCards requestMyCards = new RequestMyCards(this, BusinessCardApplication.loggedUser);
        requestMyCards.execute(new String[]{});

        RequestEvents requestEvents = new RequestEvents(this, BusinessCardApplication.loggedUser);
        requestEvents.execute(new String[]{});

        // register a receiver for new location events
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
        // check if a notification started this activity
        verifyReceivedIntent(intent);

        super.onNewIntent(intent);
    }

    /**
     * Check if a notification started this activity
     */
    private void verifyReceivedIntent(Intent intent) {
        Bundle receivedBundle = intent.getExtras();
        if (receivedBundle != null && getIntent().getExtras().containsKey(Util.REQUEST_CARD_RESPONSE_EXTRA)) {
            // this activity was started by "Business Card access request" notification
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            // dismiss the notification
            notificationManager.cancel(GcmIntentService.REQUEST_CARD_NOTIFICATION_ID);

            // extract the cardId and userId from the intent
            String cardId = getIntent().getExtras().getString(Util.REQUEST_CARD_RESPONSE_CARD_ID_EXTRA);
            String userId = getIntent().getExtras().getString(Util.REQUEST_CARD_RESPONSE_USER_ID_EXTRA);

            // verify which button was tapped
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
        } else if (receivedBundle != null && getIntent().getExtras().containsKey(Util.SHARE_CARD_RESPONSE_EXTRA)) {
            // this activity was started by "Business Card share request" notification
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            // dismiss the notification
            notificationManager.cancel(GcmIntentService.SHARE_CARD_NOTIFICATION_ID);

            // extract the cardId and userId from the intent
            String cardId = getIntent().getExtras().getString(Util.SHARE_CARD_RESPONSE_CARD_ID_EXTRA);
            String userId = getIntent().getExtras().getString(Util.SHARE_CARD_RESPONSE_USER_ID_EXTRA);

            // verify which button was tapped
            String shareCardExtra = getIntent().getExtras().getString(Util.SHARE_CARD_RESPONSE_EXTRA);
            if (shareCardExtra.equals(Util.SHARE_CARD_RESPONSE_SAVE)) {
                // Save the shared card
                displayProgressDialog();
                RequestSaveSharedCard requestSaveSharedCard = new RequestSaveSharedCard(this, cardId, userId);
                requestSaveSharedCard.execute(new String[]{});
            } else if (shareCardExtra.equals(Util.SHARE_CARD_RESPONSE_CANCEL)) {
                // Cancel the shared card
                Toast.makeText(this, R.string.cancelled_shared_card, Toast.LENGTH_SHORT).show();
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

        // set the correct visibility for the option menu elements, depending on the selected viewpager page
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
                if (Util.isNetworkAvailable(this)) {
                    // start searching for nearby public cards
                    Intent nearbyIntent = new Intent(this, NearbyCardsActivity.class);
                    startActivity(nearbyIntent);
                } else {
                    (new Util()).displayInternetRequiredDialog(this);
                }

                break;
            case R.id.action_add:
                if (Util.isNetworkAvailable(this)) {
                    if (currentPage == 1) {
                        // Clear any Business Card selected
                        BusinessCardApplication.selectedBusinessCard = null;
                        // start the edit card activity
                        Intent addEditCardIntent = new Intent(this, AddEditCardActivity.class);
                        startActivity(addEditCardIntent);
                    } else if (currentPage == 2) {
                        // add a Event
                        Intent createEventIntent = new Intent(this, CreateEventActivity.class);
                        startActivity(createEventIntent);
                    }
                } else {
                    (new Util()).displayInternetRequiredDialog(this);
                }

                break;
            case R.id.action_join:
                if (Util.isNetworkAvailable(this)) {
                    // Join an Event
                    displayJoinEventDialog();
                } else {
                    (new Util()).displayInternetRequiredDialog(this);
                }

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
            // stop receiving new location events
            unregisterReceiver(lftBroadcastReceiver);
        }
    }

    public List<BusinessCard> getSavedCards() {
        return savedCards;
    }

    public List<BusinessCard> getMyCards() {
        return myCards;
    }

    public List<Event> getEvents() {
        return events;
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

        // update the Saved Cards fragment
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

        // update the My Cards fragment
        ((MyCardsFragment) pagerAdapter.getItem(1)).setMyCards(businessCards);
    }

    /**
     * Finished request for Events
     */
    public void onEventsRequestFinished(JSONArray j) {
        events = new ArrayList<Event>();
        List<Event> events = new ArrayList<Event>();
        for (int i = 0; i < j.length(); i++) {
            try {
                Event event = Event.parseEventFromJson(j.getJSONObject(i));
                events.add(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        this.events = events;

        // save the list to cache file
        Util.saveList(this.events, Util.EVENTS_FILE);

        // update the Events fragment
        ((EventsFragment) pagerAdapter.getItem(2)).setMyEvents(this.events);
    }

    // GCM registration is complete
    public void onGCMRegistrationComplete(String msg) {
        Log.d("MainActivity", "msg: " + msg);

        // update the server with the GCM reg id
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
                // gcm red id updated
                Log.d("MainActivity", "Device GCM registration id sent to server");
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

                // remove the card from the My Cards fragment
                myCards.remove(BusinessCardApplication.selectedBusinessCard);
                // save the list to cache file
                Util.saveList(this.myCards, Util.MY_CARDS_FILE);

                // refresh the my cards list
                RequestMyCards requestMyCards = new RequestMyCards(this, BusinessCardApplication.loggedUser);
                requestMyCards.execute(new String[]{});

                // remove the card from the fragment
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

                // remove the card from the Saved Cards fragment
                savedCards.remove(BusinessCardApplication.selectedBusinessCard);
                // save the list to cache file
                Util.saveList(this.savedCards, Util.SAVED_CARDS_FILE);

                // refresh the saved cards list
                RequestSavedCards requestSavedCards = new RequestSavedCards(this, BusinessCardApplication.loggedUser);
                requestSavedCards.execute(new String[]{});

                // remove the card from the fragment
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

                // remove the event from the Events fragment
                events.remove(BusinessCardApplication.selectedEvent);
                // save the list to cache file
                Util.saveList(this.events, Util.EVENTS_FILE);

                ((EventsFragment) pagerAdapter.getItem(2)).removeSelectedEvent();

                // refresh the events list
                RequestEvents requestEvents = new RequestEvents(this, BusinessCardApplication.loggedUser);
                requestEvents.execute(new String[]{});
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
                    // invalid passcode
                    Toast.makeText(MainActivity.this, R.string.invalid_passcode, Toast.LENGTH_SHORT).show();
                    return;
                } else if (value.length() < 4) {
                    // passcode too short
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

                // refresh the events list
                RequestEvents requestEvents = new RequestEvents(this, BusinessCardApplication.loggedUser);
                requestEvents.execute(new String[]{});
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

    /**
     * Finished request for share users
     */
    public void onShareUsersRequestFinished(JSONArray j) {
        progressDialog.dismiss();

        List<User> shareUsers = new ArrayList<User>();
        for (int i = 0; i < j.length(); i++) {
            try {
                User shareUser = User.parseUserFromJson(j.getJSONObject(i));
                shareUsers.add(shareUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (shareUsers.size() > 0) {
            displayShareUsersDialog(shareUsers);
        } else {
            Toast.makeText(this, getString(R.string.no_share_users), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayShareUsersDialog(final List<User> shareUsers) {
        // display a list of users to which a card can be shared
        String[] users = new String[shareUsers.size()];
        for (int i = 0; i < shareUsers.size(); i++) {
            users[i] = shareUsers.get(i).getFirstName() + " " + shareUsers.get(i).getLastName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_user)
                .setItems(users, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        User selectedUser = shareUsers.get(which);
                        BusinessCardApplication.selectedUser = selectedUser;

                        progressDialog.show();
                        RequestShareCard requestShareCard = new RequestShareCard(MainActivity.this,
                                BusinessCardApplication.selectedBusinessCard, selectedUser.getId());
                        requestShareCard.execute(new String[]{});
                    }
                });

        builder.show();
    }

    /**
     * Finished sharing a business card
     */
    public void onShareCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            int success = json.getInt("success");
            if (success == 1) {
                // card shared
                Toast.makeText(this, getString(R.string.shared_cared,
                        BusinessCardApplication.selectedBusinessCard.getTitle(),
                        BusinessCardApplication.selectedUser.getFirstName(),
                        BusinessCardApplication.selectedUser.getLastName()), Toast.LENGTH_LONG).show();
            } else {
                // card not shared
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finished saving the shared card
     */
    public void onSaveSharedCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // Card saved
                Toast.makeText(this, R.string.card_saved, Toast.LENGTH_SHORT).show();

                RequestSavedCards requestSavedCards = new RequestSavedCards(this, BusinessCardApplication.loggedUser);
                requestSavedCards.execute(new String[]{});
            } else {
                // card not saved
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pager adapter class
     */
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
                    // Saved Cards fragment
                    if (savedCardsFragment == null) {
                        savedCardsFragment = new SavedCardsFragment();
                    }
                    return savedCardsFragment;
                case 1:
                    // My Cards fragment
                    if (myCardsFragment == null) {
                        myCardsFragment = new MyCardsFragment();
                    }
                    return myCardsFragment;
                case 2:
                    // Events fragment
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
                    // Saved Cards fragment
                    return getString(R.string.saved_cards);
                case 1:
                    // My Cards fragment
                    return getString(R.string.my_cards);
                case 2:
                    return getString(R.string.events);
                default:
                    // Events fragment
                    return null;
            }
        }
    }
}