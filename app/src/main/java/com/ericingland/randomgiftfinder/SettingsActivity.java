package com.ericingland.randomgiftfinder;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment, new SettingsFragment())
                .commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }

    public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle paramBundle) {
            super.onCreate(paramBundle);
            addPreferencesFromResource(R.xml.pref_settings);

            ListPreference department = (ListPreference) findPreference("child_filter_department_preference");
            department.setSummary(department.getEntry());
            EditTextPreference maxPrice = (EditTextPreference) findPreference("child_filter_max_price_preference");
            maxPrice.setSummary(maxPrice.getText());
            EditTextPreference minPrice = (EditTextPreference) findPreference("child_filter_min_price_preference");
            minPrice.setSummary(minPrice.getText());
            EditTextPreference staticTerms = (EditTextPreference) findPreference("child_filter_static_terms_preference");
            staticTerms.setSummary(staticTerms.getText());

            Preference clearHistory = findPreference("clear_history_preference");
            clearHistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DatabaseHelper db = new DatabaseHelper(getActivity());
                    db.deleteAllItem();
                    Toast toast = Toast.makeText(getActivity(), "History cleared.", Toast.LENGTH_LONG);
                    toast.show();
                    return true;
                }
            });

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
         /* get preference */
            Preference preference = findPreference(key);

        /* update summary */
            switch (key) {
                case "child_filter_department_preference":
                    preference.setSummary(((ListPreference) preference).getEntry());
                    break;
                case "child_filter_max_price_preference":
                    preference.setSummary(((EditTextPreference) preference).getText());
                    break;
                case "child_filter_min_price_preference":
                    preference.setSummary(((EditTextPreference) preference).getText());
                    break;
                case "child_filter_static_terms_preference":
                    preference.setSummary(((EditTextPreference) preference).getText());
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }

}
