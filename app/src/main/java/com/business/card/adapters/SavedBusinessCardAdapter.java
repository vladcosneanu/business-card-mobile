package com.business.card.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.business.card.R;
import com.business.card.objects.BusinessCard;
import com.business.card.util.Util;

import java.util.List;

public class SavedBusinessCardAdapter extends BaseAdapter {

    private Context context;
    private List<BusinessCard> businessCards;

    public SavedBusinessCardAdapter(Context context, List<BusinessCard> businessCards) {
        this.context = context;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // inflate the corresponding row layout
            rowView = inflater.inflate(R.layout.saved_business_card_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.cardView = (CardView) rowView.findViewById(R.id.card_view);
            viewHolder.name = (TextView) rowView.findViewById(R.id.name);
            viewHolder.phone = (TextView) rowView.findViewById(R.id.phone);
            viewHolder.title = (TextView) rowView.findViewById(R.id.title);
            viewHolder.email = (TextView) rowView.findViewById(R.id.email);

            rowView.setTag(viewHolder);
        }

        // populate the UI elements with the correct information
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.cardView.setCardBackgroundColor(context.getResources()
                .getColor(Util.getColorByCardLayoutNo(Integer.parseInt(businessCard.getLayout()))));

        viewHolder.title.setText(businessCard.getTitle());
        viewHolder.phone.setText(businessCard.getPhone());

        viewHolder.name.setText(businessCard.getFirstName() + " " + businessCard.getLastName());

        if (businessCard.getEmail().equals("")) {
            viewHolder.email.setVisibility(View.GONE);
        } else {
            viewHolder.email.setVisibility(View.VISIBLE);
            viewHolder.email.setText(businessCard.getEmail());
        }

        return rowView;
    }

    // this class is a holder for UI elements, in order to efficiently reuse them
    static class ViewHolder {
        private TextView name;
        private TextView phone;
        private TextView title;
        private TextView email;
        private CardView cardView;
    }
}
