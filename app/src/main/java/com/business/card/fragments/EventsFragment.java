package com.business.card.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.activities.EventCardsActivity;
import com.business.card.activities.MainActivity;
import com.business.card.adapters.EventAdapter;
import com.business.card.objects.BusinessCard;
import com.business.card.objects.Event;
import com.business.card.requests.RequestDeleteJoinedEvent;
import com.business.card.util.Util;

import java.util.List;

public class EventsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View mView;
    private List<Event> myEvents;
    private ListView myEventsListView;
    private EventAdapter adapter;
    private ProgressBar progressBar;
    private TextView noEventsAvailable;
    private boolean refreshList = false;
    private Event selectedEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.events, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        myEventsListView = (ListView) mView.findViewById(R.id.events_listview);
        myEventsListView.setVisibility(View.GONE);

        noEventsAvailable = (TextView) mView.findViewById(R.id.no_events_available);
        noEventsAvailable.setVisibility(View.GONE);

        if (refreshList) {
            refreshList = false;
            setMyEvents(myEvents);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).getEvents() != null) {
            setMyEvents(((MainActivity) getActivity()).getEvents());
        }
    }

    public void setMyEvents(List<Event> myEvents) {
        if (!isAdded()) {
            this.myEvents = myEvents;
            refreshList = true;
            return;
        }

        progressBar.setVisibility(View.GONE);

        if (myEvents.size() > 0) {
            myEventsListView.setVisibility(View.VISIBLE);
            noEventsAvailable.setVisibility(View.GONE);
            adapter = new EventAdapter(getActivity(), myEvents);
            myEventsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            myEventsListView.setOnItemClickListener(this);

            registerForContextMenu(myEventsListView);
        } else {
            myEventsListView.setVisibility(View.GONE);
            noEventsAvailable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Event event = adapter.getItem(position);
        BusinessCardApplication.selectedEvent = event;

        if (Util.isNetworkAvailable(getActivity())) {
            // start the activity for viewing the cards from this event
            Intent intent = new Intent(getActivity(), EventCardsActivity.class);
            startActivity(intent);
        } else {
            (new Util()).displayInternetRequiredCustomDialog(getActivity(), R.string.internet_required_events_message);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedEvent = (Event) myEventsListView.getAdapter().getItem(info.position);
        BusinessCardApplication.selectedEvent = selectedEvent;

        menu.setHeaderTitle(getString(R.string.choose_action_for_event));

        // add context menu items - second parameter is the itemId
        menu.add(0, Util.CONTEXT_MENU_ITEM_EVENTS_REMOVE, 0, getString(R.string.remove));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Util.CONTEXT_MENU_ITEM_EVENTS_REMOVE:
                if (Util.isNetworkAvailable(getActivity())) {
                    // selected Remove
                    displayConfirmRemoveDialog(selectedEvent);
                } else {
                    (new Util()).displayInternetRequiredCustomDialog(getActivity(), R.string.internet_required_event_remove_message);
                }

                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void displayConfirmRemoveDialog(final Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.remove_event);
        builder.setMessage(getString(R.string.remove_event_message, event.getName(),
                event.getLocation()));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Yes" button, delete the selected event
                ((MainActivity) getActivity()).displayProgressDialog();
                RequestDeleteJoinedEvent requestDeleteJoinedEvent = new RequestDeleteJoinedEvent((MainActivity) getActivity(), event);
                requestDeleteJoinedEvent.execute(new String[]{});
            }
        });

        builder.setNegativeButton(R.string.no, null);

        builder.show();
    }

    public void removeSelectedEvent() {
        // remove the Event that the user deleted
        myEvents.remove(selectedEvent);

        adapter = new EventAdapter(getActivity(), myEvents);
        myEventsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
