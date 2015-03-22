package com.business.card.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.business.card.R;

public class SavedCardsFragment extends Fragment {

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.saved_cards, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
}
