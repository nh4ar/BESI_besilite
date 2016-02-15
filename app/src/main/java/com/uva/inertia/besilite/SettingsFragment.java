package com.uva.inertia.besilite;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Ben on 2/10/2016.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}