package com.example.mihirnewalkar.myteleprompter;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference preference = findPreference("pref_test");
        Intent intent = new Intent(getActivity(), TelepromptActivity.class);
        intent.putExtra("data", getString(R.string.blind_text));
        preference.setIntent(intent);
    }
}
