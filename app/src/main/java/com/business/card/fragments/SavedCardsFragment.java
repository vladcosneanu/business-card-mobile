package com.business.card.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.business.card.R;
import com.business.card.activities.MainActivity;
import com.business.card.adapters.SavedBusinessCardAdapter;
import com.business.card.objects.BusinessCard;

import java.util.List;

public class SavedCardsFragment extends Fragment {

    private View mView;
    private List<BusinessCard> savedCards;
    private ListView savedCardsListView;
    private SavedBusinessCardAdapter adapter;
    private ProgressBar progressBar;
    private TextView noCardsAvailable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.saved_cards, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        savedCardsListView = (ListView) mView.findViewById(R.id.saved_cards_listview);
        savedCardsListView.setVisibility(View.GONE);

        noCardsAvailable = (TextView) mView.findViewById(R.id.no_cards_available);
        noCardsAvailable.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (((MainActivity) getActivity()).getSavedCards() != null) {
            setSavedCards(((MainActivity) getActivity()).getSavedCards());
        }
    }

    public void setSavedCards(List<BusinessCard> savedCards) {
        progressBar.setVisibility(View.GONE);

        this.savedCards = savedCards;

        if (savedCards.size() > 0) {
            savedCardsListView.setVisibility(View.VISIBLE);
            noCardsAvailable.setVisibility(View.GONE);
            adapter = new SavedBusinessCardAdapter(getActivity(), savedCards);
            savedCardsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            savedCardsListView.setVisibility(View.GONE);
            noCardsAvailable.setVisibility(View.VISIBLE);
        }
    }
}
