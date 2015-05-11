package com.business.card.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
        email = (TextView) findViewById(R.id.email);
        if (businessCard.getEmail() != null && !businessCard.getEmail().equals("")) {
            email.setText(businessCard.getEmail());
        } else {
            email.setVisibility(View.GONE);
        }
        address = (TextView) findViewById(R.id.address);
        if (businessCard.getAddress() != null && !businessCard.getAddress().equals("")) {
            address.setText(businessCard.getAddress());
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
