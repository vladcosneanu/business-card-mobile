package com.business.card.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.business.card.R;
import com.business.card.util.Util;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // set up the nearby preference
        final ListPreference nearby = (ListPreference) findPreference("nearby");
        nearby.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final int index = nearby.findIndexOfValue(newValue.toString());
                if (index >= 0) {
                    final String summary = (String) nearby.getEntries()[index];
                    nearby.setSummary(summary);
                }

                return true;
            }
        });

        // set up the logout preference
        final Preference logout = (Preference) findPreference("logout");
        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                (new Util()).displayConfirmLogoutDialog(getActivity());

                return false;
            }
        });
    }
}
