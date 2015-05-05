package com.business.card.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.business.card.R;
import com.business.card.util.PreferenceHelper;


public class NotLoggedActivity extends ActionBarActivity {

    private Button createAccountButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        // get a reference to the "Create a new account" button and assign a click listener
        createAccountButton = (Button) findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the activity to create a new account
                Intent intent = new Intent(NotLoggedActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        // get a reference to the "Log in using and existing account" button and assign a click listener
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the activity to log in with an existing account
                Intent intent = new Intent(NotLoggedActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
