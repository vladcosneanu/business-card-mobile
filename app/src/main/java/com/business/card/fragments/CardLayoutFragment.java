package com.business.card.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.objects.BusinessCard;

public class CardLayoutFragment extends Fragment {

    public static final String LAYOUT_KEY = "LAYOUT_KEY";

    private View mView;
    private TextView firstName;
    private TextView lastName;
    private TextView title;
    private TextView phone;
    private TextView email;
    private TextView address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int cardLayout = bundle.getInt(LAYOUT_KEY, R.layout.card_layout_1);
        mView = inflater.inflate(cardLayout, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (BusinessCardApplication.selectedBusinessCard != null) {
            BusinessCard businessCard = BusinessCardApplication.selectedBusinessCard;

            firstName = (TextView) mView.findViewById(R.id.first_name);
            firstName.setText(businessCard.getFirstName());
            lastName = (TextView) mView.findViewById(R.id.last_name);
            lastName.setText(businessCard.getLastName());
            title = (TextView) mView.findViewById(R.id.title);
            title.setText(businessCard.getTitle());
            phone = (TextView) mView.findViewById(R.id.phone);
            phone.setText(businessCard.getPhone());
            email = (TextView) mView.findViewById(R.id.email);
            if (businessCard.getEmail() != null && !businessCard.getEmail().equals("")) {
                email.setText(businessCard.getEmail());
            } else {
                email.setVisibility(View.GONE);
            }
            address = (TextView) mView.findViewById(R.id.address);
            if (businessCard.getAddress() != null && !businessCard.getAddress().equals("")) {
                address.setText(businessCard.getAddress());
            } else {
                address.setVisibility(View.GONE);
            }
        }
    }
}
