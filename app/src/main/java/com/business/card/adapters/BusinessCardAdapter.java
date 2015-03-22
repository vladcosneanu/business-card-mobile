package com.business.card.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.business.card.R;
import com.business.card.objects.BusinessCard;

import java.util.List;

public class BusinessCardAdapter extends BaseAdapter {

    private Context context;
    private List<BusinessCard> businessCards;

    public BusinessCardAdapter(Context context, List<BusinessCard> businessCards) {
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

            rowView = inflater.inflate(R.layout.business_card_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.title = (TextView) rowView.findViewById(R.id.title);
            viewHolder.phone = (TextView) rowView.findViewById(R.id.phone);
            viewHolder.email = (TextView) rowView.findViewById(R.id.email);
            viewHolder.address = (TextView) rowView.findViewById(R.id.address);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.title.setText(businessCard.getTitle());
        viewHolder.phone.setText(businessCard.getPhone());
        viewHolder.email.setText(businessCard.getEmail());
        viewHolder.address.setText(businessCard.getAddress());

        return rowView;
    }

    static class ViewHolder {
        private TextView title;
        private TextView phone;
        private TextView email;
        private TextView address;
    }
}
