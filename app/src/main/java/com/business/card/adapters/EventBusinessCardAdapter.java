package com.business.card.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.business.card.R;
import com.business.card.activities.EventCardsActivity;
import com.business.card.objects.BusinessCard;
import com.business.card.util.Util;

import java.util.List;

public class EventBusinessCardAdapter extends BaseAdapter {

    private EventCardsActivity activity;
    private List<BusinessCard> businessCards;

    public EventBusinessCardAdapter(EventCardsActivity activity, List<BusinessCard> businessCards) {
        this.activity = activity;
        this.businessCards = businessCards;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public int getCount() {
        return businessCards.size();
    }

    public BusinessCard getItem(int position) {
        return businessCards.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BusinessCard businessCard = getItem(position);
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.event_business_card_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.cardView = (CardView) rowView.findViewById(R.id.card_view);
            viewHolder.name = (TextView) rowView.findViewById(R.id.name);
            viewHolder.title = (TextView) rowView.findViewById(R.id.title);
            viewHolder.email = (TextView) rowView.findViewById(R.id.email);
            viewHolder.phone = (TextView) rowView.findViewById(R.id.phone);
            viewHolder.address = (TextView) rowView.findViewById(R.id.address);
            viewHolder.visibility = (TextView) rowView.findViewById(R.id.visibility);
            viewHolder.requested = (TextView) rowView.findViewById(R.id.requested);
            viewHolder.saveCardButton = (Button) rowView.findViewById(R.id.save_card_button);
            viewHolder.requestCardButton = (Button) rowView.findViewById(R.id.request_card_button);

            rowView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.cardView.setCardBackgroundColor(activity.getResources()
                .getColor(Util.getColorByCardLayoutNo(Integer.parseInt(businessCard.getLayout()))));

        viewHolder.title.setText(businessCard.getTitle());
        viewHolder.name.setText(businessCard.getFirstName() + " " + businessCard.getLastName());

        viewHolder.requested.setVisibility(View.GONE);

        if (businessCard.getIsPublic().equals("1")) {
            if (businessCard.getEmail().equals("")) {
                viewHolder.email.setVisibility(View.GONE);
            } else {
                viewHolder.email.setVisibility(View.VISIBLE);
                viewHolder.email.setText(businessCard.getEmail());
            }

            if (businessCard.getAddress().equals("")) {
                viewHolder.address.setVisibility(View.GONE);
            } else {
                viewHolder.address.setVisibility(View.VISIBLE);
                viewHolder.address.setText(businessCard.getAddress());
            }

            viewHolder.phone.setVisibility(View.VISIBLE);
            viewHolder.phone.setText(businessCard.getPhone());

            viewHolder.visibility.setText(activity.getString(R.string.public_text));
            viewHolder.saveCardButton.setVisibility(View.VISIBLE);
            viewHolder.requestCardButton.setVisibility(View.GONE);

            viewHolder.saveCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Util.isNetworkAvailable(activity)) {
                        // "Save Card" button was pressed for this card
                        activity.requestPublicEventCard(businessCard);
                    } else {
                        (new Util()).displayInternetRequiredCustomDialog(activity, R.string.internet_required_card_save_message);
                    }
                }
            });
        } else {
            viewHolder.email.setVisibility(View.GONE);
            viewHolder.address.setVisibility(View.GONE);
            viewHolder.phone.setVisibility(View.GONE);

            viewHolder.visibility.setText(activity.getString(R.string.private_text));
            viewHolder.saveCardButton.setVisibility(View.GONE);
            viewHolder.requestCardButton.setVisibility(View.VISIBLE);

            viewHolder.requestCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Util.isNetworkAvailable(activity)) {
                        // "Request Card" button was pressed for this card
                        businessCard.setRequested(true);
                        viewHolder.requestCardButton.setVisibility(View.GONE);
                        viewHolder.requested.setVisibility(View.VISIBLE);

                        activity.requestPrivateEventCard(businessCard);
                    } else {
                        (new Util()).displayInternetRequiredCustomDialog(activity, R.string.internet_required_card_request_message);
                    }
                }
            });

            if (businessCard.isRequested()) {
                // "Request Card" button was pressed for this card
                viewHolder.requestCardButton.setVisibility(View.GONE);
                viewHolder.requested.setVisibility(View.VISIBLE);
            } else {
                // "Request Card" button was not pressed for this card
                viewHolder.requestCardButton.setVisibility(View.VISIBLE);
                viewHolder.requested.setVisibility(View.GONE);
            }
        }

        return rowView;
    }

    static class ViewHolder {
        private TextView name;
        private TextView title;
        private TextView email;
        private TextView phone;
        private TextView address;
        private TextView visibility;
        private TextView requested;
        private Button saveCardButton;
        private Button requestCardButton;
        private CardView cardView;
    }
}
