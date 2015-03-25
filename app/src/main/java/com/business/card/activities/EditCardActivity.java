package com.business.card.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.objects.BusinessCard;
import com.business.card.requests.RequestEditCard;
import com.business.card.requests.RequestSignUp;
import com.business.card.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class EditCardActivity extends ActionBarActivity {

    private BusinessCard businessCard;

    private EditText title;
    private EditText email;
    private EditText phone;
    private EditText address;
    private Button editCardButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_card);

        businessCard = BusinessCardApplication.selectedBusinessCard;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references to the text edit fields and fill them wih the selected business card info
        title = (EditText) findViewById(R.id.title);
        title.setText(businessCard.getTitle());
        email = (EditText) findViewById(R.id.email);
        email.setText(businessCard.getEmail());
        phone = (EditText) findViewById(R.id.phone);
        phone.setText(businessCard.getPhone());
        address = (EditText) findViewById(R.id.address);
        address.setText(businessCard.getAddress());

        // get a reference to the "Edit Business Card" button and assign a click listener
        editCardButton = (Button) findViewById(R.id.edit_card_button);
        editCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the account creation process
                String titleValue = title.getText().toString().trim();
                String emailValue = email.getText().toString().trim();
                String phoneValue = phone.getText().toString().trim();
                String addressValue = address.getText().toString().trim();

                if (titleValue.equals("") || emailValue.equals("") || phoneValue.equals("")) {
                    // fields not completed
                    Toast.makeText(EditCardActivity.this, getString(R.string.please_fill_required_fields), Toast.LENGTH_SHORT).show();
                } else if (!Util.isEmailValid(emailValue)) {
                    // email address is invalid
                    Toast.makeText(EditCardActivity.this, getString(R.string.email_invalid), Toast.LENGTH_SHORT).show();
                } else {
                    // all is good
                    progressDialog.show();

                    BusinessCard editedBusinessCard = new BusinessCard();
                    editedBusinessCard.setId(businessCard.getId());
                    editedBusinessCard.setTitle(titleValue);
                    editedBusinessCard.setEmail(emailValue);
                    editedBusinessCard.setPhone(phoneValue);
                    editedBusinessCard.setAddress(addressValue);

                    RequestEditCard requestEditCard = new RequestEditCard(EditCardActivity.this, editedBusinessCard);
                    requestEditCard.execute(new String[] {});
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // the top left back button was clicked
                finish();
                break;
        }

        return true;
    }

    public void onEditCardRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // card edited
                Toast.makeText(EditCardActivity.this, getString(R.string.card_edit_success), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // card not edited
                Toast.makeText(EditCardActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
