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
import com.business.card.activities.MainActivity;
import com.business.card.activities.ViewCardActivity;
import com.business.card.adapters.MyBusinessCardAdapter;
import com.business.card.objects.BusinessCard;
import com.business.card.requests.RequestDeleteMyCard;
import com.business.card.requests.RequestNearbyCards;
import com.business.card.requests.RequestShareUsers;
import com.business.card.util.PreferenceHelper;
import com.business.card.util.Util;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

import java.util.List;

public class MyCardsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private View mView;
    private List<BusinessCard> myCards;
    private ListView myCardsListView;
    private MyBusinessCardAdapter adapter;
    private ProgressBar progressBar;
    private TextView noCardsAvailable;
    private boolean refreshList = false;
    private BusinessCard selectedBusinessCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the fragment layout
        mView = inflater.inflate(R.layout.my_cards, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // get references to the UI elements
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        myCardsListView = (ListView) mView.findViewById(R.id.my_cards_listview);
        myCardsListView.setVisibility(View.GONE);

        noCardsAvailable = (TextView) mView.findViewById(R.id.no_cards_available);
        noCardsAvailable.setVisibility(View.GONE);

        if (refreshList) {
            refreshList = false;
            setMyCards(myCards);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // set up the my cards list
        if (((MainActivity) getActivity()).getMyCards() != null) {
            setMyCards(((MainActivity) getActivity()).getMyCards());
        }
    }

    /**
     * List item was tapped
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // get the tapped card and set it as selected
        BusinessCard businessCard = adapter.getItem(position);
        businessCard.setFirstName(BusinessCardApplication.loggedUser.getFirstName());
        businessCard.setLastName(BusinessCardApplication.loggedUser.getLastName());
        BusinessCardApplication.selectedBusinessCard = businessCard;

        // start the view card activity
        Intent intent = new Intent(getActivity(), ViewCardActivity.class);
        intent.putExtra(ViewCardActivity.DISPLAY_EDIT_EXTRA_KEY, true);
        startActivity(intent);
    }

    /**
     * Method called before displaying the long tap menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // get the card that was long tapped
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedBusinessCard = (BusinessCard) myCardsListView.getAdapter().getItem(info.position);
        selectedBusinessCard.setFirstName(BusinessCardApplication.loggedUser.getFirstName());
        selectedBusinessCard.setLastName(BusinessCardApplication.loggedUser.getLastName());

        menu.setHeaderTitle(getString(R.string.choose_action_for_card));

        // add context menu items - second parameter is the itemId
        menu.add(0, Util.CONTEXT_MENU_ITEM_MY_CARDS_SHARE, 0, getString(R.string.share));
        menu.add(0, Util.CONTEXT_MENU_ITEM_MY_CARDS_EDIT, 0, getString(R.string.edit));
        menu.add(0, Util.CONTEXT_MENU_ITEM_MY_CARDS_DELETE, 0, getString(R.string.delete));
    }

    /**
     * Item seleted form long tap menu
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Util.CONTEXT_MENU_ITEM_MY_CARDS_SHARE:
                if (Util.isNetworkAvailable(getActivity())) {
                    // selected Share
                    BusinessCardApplication.selectedBusinessCard = selectedBusinessCard;
                    ((MainActivity) getActivity()).displayProgressDialog();

                // get the latest location
                    LocationInfo latestInfo = new LocationInfo(getActivity());
                    Util.updateCoordinate(latestInfo);

                // start the request fot the available share users
                    RequestShareUsers requestShareUsers = new RequestShareUsers((MainActivity) getActivity(), BusinessCardApplication.loggedUser, Util
                            .getLocation(), PreferenceHelper.getNearbyRadius(getActivity()));
                    requestShareUsers.execute(new String[]{});
                } else {
                    (new Util()).displayInternetRequiredCustomDialog(getActivity(), R.string.internet_required_card_share_message);
                }

                break;
            case Util.CONTEXT_MENU_ITEM_MY_CARDS_EDIT:
                if (Util.isNetworkAvailable(getActivity())) {
                    // selected Edit
                    BusinessCardApplication.selectedBusinessCard = selectedBusinessCard;

                    // start the edit card activity
                    Intent intent = new Intent(getActivity(), AddEditCardActivity.class);
                    startActivity(intent);
                } else {
                    (new Util()).displayInternetRequiredCustomDialog(getActivity(), R.string.internet_required_card_edit_message);
                }

                break;
            case Util.CONTEXT_MENU_ITEM_MY_CARDS_DELETE:
                if (Util.isNetworkAvailable(getActivity())) {
                    // selected Delete
                    displayConfirmDeleteDialog(selectedBusinessCard);
                } else {
                    (new Util()).displayInternetRequiredCustomDialog(getActivity(), R.string.internet_required_card_delete_message);
                }

                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void displayConfirmDeleteDialog(final BusinessCard businessCard) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_card);
        builder.setMessage(getString(R.string.delete_card_message, businessCard.getTitle(),
                businessCard.getEmail(), businessCard.getPhone(), businessCard.getAddress()));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Yes" button, delete the selected business card
                ((MainActivity) getActivity()).displayProgressDialog();
                RequestDeleteMyCard requestDeleteMyCard = new RequestDeleteMyCard((MainActivity) getActivity(), businessCard);
                requestDeleteMyCard.execute(new String[]{});
            }
        });

        builder.setNegativeButton(R.string.no, null);

        builder.show();
    }

    public void setMyCards(List<BusinessCard> myCards) {
        if (!isAdded()) {
            // layout was not yet inflated, save the cards in order to populate the list
            // when the layout is ready
            this.myCards = myCards;
            refreshList = true;
            return;
        }

        progressBar.setVisibility(View.GONE);

        if (myCards.size() > 0) {
            // cards are available
            myCardsListView.setVisibility(View.VISIBLE);
            noCardsAvailable.setVisibility(View.GONE);
            adapter = new MyBusinessCardAdapter(getActivity(), myCards);
            myCardsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            // set the list item tap listener
            myCardsListView.setOnItemClickListener(this);

            // register the list for long tap events
            registerForContextMenu(myCardsListView);
        } else {
            // no cards available
            myCardsListView.setVisibility(View.GONE);
            noCardsAvailable.setVisibility(View.VISIBLE);
        }
    }

    public void removeSelectedBusinessCard() {
        // remove the Business Card that the user deleted
        if (myCards == null) {
            myCards = ((MainActivity) getActivity()).getMyCards();
        }
        myCards.remove(selectedBusinessCard);

        adapter = new MyBusinessCardAdapter(getActivity(), myCards);
        myCardsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
