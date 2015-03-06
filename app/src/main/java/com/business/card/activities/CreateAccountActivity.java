package com.business.card.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.business.card.BusinessCardApplication;
import com.business.card.R;
import com.business.card.objects.User;
import com.business.card.requests.RequestSignUp;
import com.business.card.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class CreateAccountActivity extends ActionBarActivity {

    private ImageView logo;
    private EditText title;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText phone;
    private EditText userName;
    private EditText password;
    private EditText passwordConfirm;
    private Button createAccountButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

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
            String titleValue = title.getText().toString().trim();
            String firstNameValue = firstName.getText().toString().trim();
            String lastNameValue = lastName.getText().toString().trim();
            String emailValue = email.getText().toString().trim();
            String phoneValue = phone.getText().toString().trim();
            String usernameValue = userName.getText().toString().trim();
            String passwordValue = password.getText().toString().trim();
            String confirmValue = passwordConfirm.getText().toString().trim();

            if (titleValue.equals("") || firstNameValue.equals("") || lastNameValue.equals("") ||
                usernameValue.equals("") || passwordValue.equals("") || confirmValue.equals("")) {
                // fields not completed
                Toast.makeText(CreateAccountActivity.this, getString(R.string.please_fill_required_fields), Toast.LENGTH_SHORT).show();
            } else if (!Util.isEmailValid(emailValue)) {
                // email address is invalid
                Toast.makeText(CreateAccountActivity.this, getString(R.string.login_invalid), Toast.LENGTH_SHORT).show();
            } else if (!passwordValue.equals(confirmValue)) {
                // password not confirmed
                Toast.makeText(CreateAccountActivity.this, getString(R.string.password_not_confirmed), Toast.LENGTH_SHORT).show();
            } else if (usernameValue.contains(" ")) {
                // username contains spaces
                Toast.makeText(CreateAccountActivity.this, getString(R.string.username_contains_spaces), Toast.LENGTH_SHORT).show();
            } else {
                // all is good
                progressDialog.show();

                User user = new User();
                user.setTitle(titleValue);
                user.setFirstName(firstNameValue);
                user.setLastName(lastNameValue);
                user.setUsername(usernameValue);
                user.setPassword(passwordValue);

                RequestSignUp requestSignUp = new RequestSignUp(CreateAccountActivity.this, user);
                requestSignUp.execute(new String[] {});
            }
            }
        });

        // Set up an account generator, based on random values
        setUpAccountGenerator();
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

    /**
     * Sign-up request finished
     */
    public void onSignUpRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // sign up successful
                Toast.makeText(CreateAccountActivity.this, getString(R.string.sign_up_success), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // email address not available
                Toast.makeText(CreateAccountActivity.this, getString(R.string.username_taken), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Hide the keyboard
     */
    private void hideKeyboard() {
        title.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
    }

    /**
     * Set up an account generator, based on random values
     */
    private void setUpAccountGenerator() {
        logo = (ImageView) findViewById(R.id.logo);
        logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // set a random title
                String[] titles ={"Coach", "Doctor", "Engineer", "Software Developer", "Architect"};
                title.setText(titles[new Random().nextInt(5)]);

                // set a random first name
                String[] firstNames ={"John", "William", "Anna", "Jessica", "Gabriel"};

                firstName.setText(firstNames[new Random().nextInt(5)]);

                // set a random last name
                String[] lastNames ={"Miller", "Brown", "Martinez", "Taylor", "Thompson"};
                lastName.setText(lastNames[new Random().nextInt(5)]);

                // set an email
                String firstAndLast = firstName.getText().toString().toLowerCase() + "." +
                        lastName.getText().toString().toLowerCase();
                email.setText(firstAndLast + "@email.com");

                // set a phone
                phone.setText("1-800-356-9377");

                // set a valid username
                String generatedUsername = firstAndLast + (new Random().nextInt(100));
                BusinessCardApplication.getInstance().generatedUsername = generatedUsername;
                userName.setText(generatedUsername);

                // set a valid password
                password.setText("password");
                passwordConfirm.setText("password");
                BusinessCardApplication.getInstance().generatedPassword = "password";

                // hide the keyboard
                hideKeyboard();

                return true;
            }
        });
    }
}
