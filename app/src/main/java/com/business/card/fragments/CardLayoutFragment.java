package com.business.card.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.business.card.R;

public class CardLayoutFragment extends Fragment {

    public static final String LAYOUT_KEY = "LAYOUT_KEY";

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int cardLayout = bundle.getInt(LAYOUT_KEY, R.layout.card_layout_1);
        mView = inflater.inflate(cardLayout, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
}
