package com.business.card.activities;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.business.card.fragments.SettingsFragment;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display the top left back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // add the Settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // back button was tapped
                SettingsActivity.this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
