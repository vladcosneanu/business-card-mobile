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
import com.business.card.adapters.MyBusinessCardAdapter;
import com.business.card.objects.BusinessCard;

import java.util.List;

public class MyCardsFragment extends Fragment {

    private View mView;
    private List<BusinessCard> myCards;
    private ListView myCardsListView;
    private MyBusinessCardAdapter adapter;
    private ProgressBar progressBar;
    private TextView noCardsAvailable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_cards, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        myCardsListView = (ListView) mView.findViewById(R.id.my_cards_listview);
        myCardsListView.setVisibility(View.GONE);

        noCardsAvailable = (TextView) mView.findViewById(R.id.no_cards_available);
        noCardsAvailable.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (((MainActivity) getActivity()).getMyCards() != null) {
            setMyCards(((MainActivity) getActivity()).getMyCards());
        }
    }

    public void setMyCards(List<BusinessCard> myCards) {
        progressBar.setVisibility(View.GONE);

        this.myCards = myCards;

        if (myCards.size() > 0) {
            myCardsListView.setVisibility(View.VISIBLE);
            noCardsAvailable.setVisibility(View.GONE);
            adapter = new MyBusinessCardAdapter(getActivity(), myCards);
            myCardsListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            myCardsListView.setVisibility(View.GONE);
            noCardsAvailable.setVisibility(View.VISIBLE);
        }
    }
}
