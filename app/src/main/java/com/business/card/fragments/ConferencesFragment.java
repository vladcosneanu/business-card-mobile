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
import com.business.card.activities.AddEditCardActivity;
import com.business.card.activities.ConferenceCardsActivity;
import com.business.card.activities.MainActivity;
import com.business.card.adapters.ConferenceAdapter;
import com.business.card.objects.Conference;
import com.business.card.util.Util;

import java.util.List;

public class ConferencesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View mView;
    private List<Conference> myConferences;
    private ListView myConferencesListView;
    private ConferenceAdapter adapter;
    private ProgressBar progressBar;
    private TextView noConferencesAvailable;

    private Conference selectedConference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.conferences, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        myConferencesListView = (ListView) mView.findViewById(R.id.conferences_listview);
        myConferencesListView.setVisibility(View.GONE);

        noConferencesAvailable = (TextView) mView.findViewById(R.id.no_conferences_available);
        noConferencesAvailable.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (((MainActivity) getActivity()).getConferences() != null) {
            setMyConferences(((MainActivity) getActivity()).getConferences());
        }
    }

    public void setMyConferences(List<Conference> myConferences) {
        if (!isAdded()) {
            return;
        }

        progressBar.setVisibility(View.GONE);

        this.myConferences = myConferences;

        if (myConferences.size() > 0) {
            myConferencesListView.setVisibility(View.VISIBLE);
            noConferencesAvailable.setVisibility(View.GONE);
            adapter = new ConferenceAdapter(getActivity(), myConferences);
            myConferencesListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            myConferencesListView.setOnItemClickListener(this);

            registerForContextMenu(myConferencesListView);
        } else {
            myConferencesListView.setVisibility(View.GONE);
            noConferencesAvailable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Conference conference = adapter.getItem(position);
        BusinessCardApplication.selectedConference = conference;

        // start the activity for viewing the cards from this conference
        Intent intent = new Intent(getActivity(), ConferenceCardsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedConference = (Conference) myConferencesListView.getAdapter().getItem(info.position);

        menu.setHeaderTitle(getString(R.string.choose_action_for_conference));

        // add context menu items - second parameter is the itemId
        menu.add(0, Util.CONTEXT_MENU_ITEM_CONFERENCES_DELETE, 0, getString(R.string.delete));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Util.CONTEXT_MENU_ITEM_CONFERENCES_DELETE:
                // selected Delete
                displayConfirmDeleteDialog(selectedConference);
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void displayConfirmDeleteDialog(final Conference conference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_conference);
        builder.setMessage(getString(R.string.delete_conference_message, conference.getName(),
                conference.getLocation()));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Yes" button, delete the selected conference
//                ((MainActivity) getActivity()).displayProgressDialog();
//                RequestDeleteMyCard requestDeleteMyCard = new RequestDeleteMyCard((MainActivity) getActivity(), businessCard);
//                requestDeleteMyCard.execute(new String[]{});
            }
        });

        builder.setNegativeButton(R.string.no, null);

        builder.show();
    }
}
