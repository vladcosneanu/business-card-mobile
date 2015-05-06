package com.business.card.activities;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.business.card.R;
import com.business.card.fragments.DatePickerFragment;
import com.business.card.objects.Event;
import com.business.card.requests.RequestCreateEvent;
import com.business.card.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class CreateEventActivity extends ActionBarActivity implements OnDateSetListener {

    private EditText name;
    private EditText location;
    private EditText passcode;
    private TextView dateView;
    private Button changeDate;
    private Button createEventButton;
    private ProgressDialog progressDialog;

    private String selectedYear;
    private String selectedMonth;
    private String selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        // get references to the text edit fields
        name = (EditText) findViewById(R.id.name);
        location = (EditText) findViewById(R.id.location);
        passcode = (EditText) findViewById(R.id.passcode);
        dateView = (TextView) findViewById(R.id.date);

        setDateDefaultValue();

        changeDate = (Button) findViewById(R.id.change_date);
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDateDialog();
            }
        });

        // get a reference to the "Create Event" button and assign a click listener
        createEventButton = (Button) findViewById(R.id.create_event);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the account creation process
                String nameValue = name.getText().toString().trim();
                String locationValue = location.getText().toString().trim();
                String passcodeValue = passcode.getText().toString().trim();

                if (nameValue.equals("") || locationValue.equals("") || passcodeValue.equals("")) {
                    // fields not completed
                    Toast.makeText(CreateEventActivity.this, getString(R.string.please_fill_required_fields), Toast.LENGTH_SHORT).show();
                } else if (passcodeValue.length() > 10) {
                    // passcode is too long
                    Toast.makeText(CreateEventActivity.this, getString(R.string.passcode_too_long), Toast.LENGTH_SHORT).show();
                } else {
                    // all is good
                    progressDialog.show();

                    Event event = new Event();
                    event.setName(nameValue);
                    event.setLocation(locationValue);
                    event.setDate(dateView.getText().toString());
                    event.setPasscode(passcodeValue);

                    RequestCreateEvent requestCreateEvent = new RequestCreateEvent(CreateEventActivity.this, event);
                    requestCreateEvent.execute(new String[]{});
                }
            }
        });
    }

    private void setDateDefaultValue() {
        Calendar calendar = Calendar.getInstance();
        selectedYear = String.valueOf(calendar.get(Calendar.YEAR));
        if ((calendar.get(Calendar.MONTH) + 1) < 10) {
            selectedMonth = "0" + (calendar.get(Calendar.MONTH) + 1);
        } else {
            selectedMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        }
        if (calendar.get(Calendar.DATE) < 10) {
            selectedDay = "0" + calendar.get(Calendar.DATE);
        } else {
            selectedDay = String.valueOf(calendar.get(Calendar.DATE));
        }
        dateView.setText(selectedYear + "-" + selectedMonth + "-" + selectedDay);
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

    private void createDateDialog() {
        DatePickerFragment date = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt("year", Integer.parseInt(selectedYear));
        args.putInt("month", Integer.parseInt(selectedMonth) - 1);
        args.putInt("day", Integer.parseInt(selectedDay));
        date.setArguments(args);

        // Set Call back to capture selected date
        date.setCallBack(this);
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        selectedYear = String.valueOf(year);
        if ((month + 1) < 10) {
            selectedMonth = "0" + (month + 1);
        } else {
            selectedMonth = String.valueOf(month + 1);
        }
        if (day < 10) {
            selectedDay = "0" + day;
        } else {
            selectedDay = String.valueOf(day);
        }
        dateView.setText(selectedYear + "-" + selectedMonth + "-" + selectedDay);
    }

    /**
     * Create event request finished
     */
    public void onCreateEventRequestFinished(JSONObject json) {
        progressDialog.dismiss();
        try {
            String success = json.getString("success");
            if (success.equals("true")) {
                // create event successful
                Toast.makeText(CreateEventActivity.this, getString(R.string.create_event_success), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // passcode not available
                Toast.makeText(CreateEventActivity.this, getString(R.string.passcode_taken), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
