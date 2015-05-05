package com.business.card.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.objects.User;
import com.business.card.requests.RequestLogin;
import com.business.card.util.PreferenceHelper;
import com.business.card.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends ActionBarActivity {

    private ImageView logo;
    private EditText username;
    private EditText password;
    private Button loginButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references to the text edit fields
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        // get a reference to the "Log in" button and assign a click listener
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the login process
                String usernameValue = username.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();
                if (usernameValue.equals("") || passwordValue.equals("")) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_empty), Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    RequestLogin requestLogin = new RequestLogin(LoginActivity.this, usernameValue, passwordValue);
                    requestLogin.execute(new String[] {});
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        setUpAutoAccountFiller();
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

    /**
     * Set up the auto account filler, based on previously generated username and password
     */
    private void setUpAutoAccountFiller() {
        logo = (ImageView) findViewById(R.id.logo);
        logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // fill in the previously generated username and password
                username.setText(BusinessCardApplication.getInstance().generatedUsername);
                password.setText(BusinessCardApplication.getInstance().generatedPassword);

                return true;
            }
        });
    }

    public void onLoginRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            if (json.getBoolean("found")) {
                User user = User.parseUserFromJson(json);
                BusinessCardApplication.loggedUser = user;
                PreferenceHelper.saveUser(user, this);

                Toast.makeText(this, R.string.login_successful, Toast.LENGTH_SHORT).show();

                // start the main activity, clearing any other activities previously opened
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.login_incorrect, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
