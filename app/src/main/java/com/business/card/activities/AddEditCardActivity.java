package com.business.card.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.objects.BusinessCard;
import com.business.card.requests.RequestAddCard;
import com.business.card.requests.RequestEditCard;
import com.business.card.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class AddEditCardActivity extends ActionBarActivity {

    private BusinessCard businessCard;

    private EditText title;
    private EditText email;
    private EditText phone;
    private EditText address;
    private CheckBox isPublicCheckbox;
    private View isPublicContainer;
    private Button addEditCardButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_card);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references to the text edit fields
        title = (EditText) findViewById(R.id.title);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        isPublicCheckbox = (CheckBox) findViewById(R.id.is_public_checkbox);
        isPublicCheckbox.setChecked(true);

        isPublicContainer = findViewById(R.id.is_public_container);
        isPublicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPublicCheckbox.toggle();
            }
        });

        if (BusinessCardApplication.selectedBusinessCard != null) {
            businessCard = BusinessCardApplication.selectedBusinessCard;

            // fill the edit fields wih the selected business card info
            title.setText(businessCard.getTitle());
            email.setText(businessCard.getEmail());
            phone.setText(businessCard.getPhone());
            address.setText(businessCard.getAddress());

            if (businessCard.getIsPublic().equals("1")) {
                isPublicCheckbox.setChecked(true);
            } else if (businessCard.getIsPublic().equals("0")) {
                isPublicCheckbox.setChecked(false);
            }
        }

        // get a reference to the "Edit Business Card" button and assign a click listener
        addEditCardButton = (Button) findViewById(R.id.add_edit_card_button);

        if (BusinessCardApplication.selectedBusinessCard != null) {
            getSupportActionBar().setTitle(getString(R.string.edit_business_card));
            addEditCardButton.setText(getString(R.string.edit_business_card));
        } else {
            getSupportActionBar().setTitle(getString(R.string.add_business_card));
            addEditCardButton.setText(getString(R.string.add_business_card));
        }

        addEditCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the account creation process
                String titleValue = title.getText().toString().trim();
                String emailValue = email.getText().toString().trim();
                String phoneValue = phone.getText().toString().trim();
                String addressValue = address.getText().toString().trim();
                String publicValue;

                if (isPublicCheckbox.isChecked()) {
                    publicValue = "1";
                } else {
                    publicValue = "0";
                }

                if (titleValue.equals("") || emailValue.equals("") || phoneValue.equals("")) {
                    // fields not completed
                    Toast.makeText(AddEditCardActivity.this, getString(R.string.please_fill_required_fields), Toast.LENGTH_SHORT).show();
                } else if (!Util.isEmailValid(emailValue)) {
                    // email address is invalid
                    Toast.makeText(AddEditCardActivity.this, getString(R.string.email_invalid), Toast.LENGTH_SHORT).show();
                } else {
                    // all is good
                    progressDialog.show();

                    BusinessCard newBusinessCard = new BusinessCard();

                    if (BusinessCardApplication.selectedBusinessCard != null) {
                        newBusinessCard.setId(businessCard.getId());
                    }

                    newBusinessCard.setUserId(BusinessCardApplication.loggedUser.getId());
                    newBusinessCard.setTitle(titleValue);
                    newBusinessCard.setEmail(emailValue);
                    newBusinessCard.setPhone(phoneValue);
                    newBusinessCard.setAddress(addressValue);
                    newBusinessCard.setIsPublic(publicValue);

                    if (BusinessCardApplication.selectedBusinessCard != null) {
                        RequestEditCard requestEditCard = new RequestEditCard(AddEditCardActivity.this, newBusinessCard);
                        requestEditCard.execute(new String[]{});
                    } else {
                        RequestAddCard requestAddCard = new RequestAddCard(AddEditCardActivity.this, newBusinessCard);
                        requestAddCard.execute(new String[]{});
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nearby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // the top left back button was clicked
                finish();
                break;
            case R.id.action_logout:
                Util.displayConfirmLogoutDialog(this);

                return true;
            default:
                break;
        }

        return true;
    }

    // Edit card request finished
    public void onEditCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // card edited
                Toast.makeText(this, getString(R.string.card_edit_success), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // card not edited
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Add card request finished
    public void onAddCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // card edited
                Toast.makeText(this, getString(R.string.card_add_success), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // card not edited
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
