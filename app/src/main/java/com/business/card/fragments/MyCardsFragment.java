package com.business.card.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.business.card.R;
import com.business.card.adapters.BusinessCardAdapter;
import com.business.card.objects.BusinessCard;

import java.util.List;

public class MyCardsFragment extends Fragment {

    private View mView;
    private List<BusinessCard> myCards;
    private ListView myCardsListView;
    private BusinessCardAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_cards, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        myCardsListView = (ListView) mView.findViewById(R.id.my_cards_listview);
    }

    public void setMyCards(List<BusinessCard> myCards) {
        this.myCards = myCards;
        Log.d("Vlad", "myCards: " + myCards.get(0).getEmail());
        adapter = new BusinessCardAdapter(getActivity(), myCards);
        myCardsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
