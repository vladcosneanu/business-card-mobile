package com.business.card.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.objects.BusinessCard;
import com.business.card.util.Util;

public class ViewCardActivity extends ActionBarActivity {

    public static final String DISPLAY_EDIT_EXTRA_KEY = "display_edit";

    private BusinessCard businessCard;
    private int cardLayout;
    private boolean displayEdit;

    private TextView firstName;
    private TextView lastName;
    private TextView title;
    private TextView phone;
    private TextView email;
    private TextView address;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayEdit = getIntent().getExtras().getBoolean(DISPLAY_EDIT_EXTRA_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        businessCard = BusinessCardApplication.selectedBusinessCard;
        cardLayout = Util.getLayoutByCardLayoutNo(Integer.parseInt(businessCard.getLayout()));

        setContentView(cardLayout);

        firstName = (TextView) findViewById(R.id.first_name);
        firstName.setText(businessCard.getFirstName());
        lastName = (TextView) findViewById(R.id.last_name);
        lastName.setText(businessCard.getLastName());
        title = (TextView) findViewById(R.id.title);
        title.setText(businessCard.getTitle());
        phone = (TextView) findViewById(R.id.phone);
        phone.setText(businessCard.getPhone());
        phone.setPaintFlags(phone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + phone.getText().toString().trim() ;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        email = (TextView) findViewById(R.id.email);
        if (businessCard.getEmail() != null && !businessCard.getEmail().equals("")) {
            email.setText(businessCard.getEmail());
            email.setPaintFlags(email.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse("mailto:?subject=&body=&to=" + email.getText().toString().trim());
                    intent.setData(data);
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }
            });
        } else {
            email.setVisibility(View.GONE);
        }
        address = (TextView) findViewById(R.id.address);
        if (businessCard.getAddress() != null && !businessCard.getAddress().equals("")) {
            address.setText(businessCard.getAddress());
            address.setPaintFlags(address.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "geo:0,0?q=" + address.getText().toString().trim();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        } else {
            address.setVisibility(View.GONE);
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(Util.getColorByCardLayoutNo(Integer.parseInt(businessCard.getLayout())))));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_card, menu);

        MenuItem editMenuItem = menu.getItem(0);
        if (displayEdit) {
            editMenuItem.setVisible(true);
        } else {
            editMenuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // the top left back button was clicked
                finish();
                break;
            case R.id.action_edit:
                // start the edit card activity
                Intent intent = new Intent(this, AddEditCardActivity.class);
                startActivity(intent);

                break;
            case R.id.action_logout:
                (new Util()).displayConfirmLogoutDialog(this);

                return true;
            case R.id.action_settings:
                // start the settings activity
                Intent settingsINtent = new Intent(this, SettingsActivity.class);
                startActivity(settingsINtent);

                break;
            default:
                break;
        }

        return true;
    }
}
