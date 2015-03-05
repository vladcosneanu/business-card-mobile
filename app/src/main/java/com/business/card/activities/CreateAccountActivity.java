package com.business.card.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.business.card.R;

public class CreateAccountActivity extends ActionBarActivity {

    private EditText title;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText phone;
    private EditText userName;
    private EditText password;
    private EditText passwordConfirm;
    private Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references to the text edit fields
        title = (EditText) findViewById(R.id.title);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        passwordConfirm = (EditText) findViewById(R.id.password_confirm);

        // get a reference to the "Create Account" button and assign a click listener
        createAccountButton = (Button) findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the account creation process

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
}
