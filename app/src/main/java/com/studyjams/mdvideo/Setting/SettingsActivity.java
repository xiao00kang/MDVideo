package com.studyjams.mdvideo.Setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.studyjams.mdvideo.Data.source.remote.SyncService;
import com.studyjams.mdvideo.R;
import com.studyjams.mdvideo.Util.D;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    /**记录扫描的路径是否改变，只在设置页面退出时扫描**/
    private static boolean isLoadChanged = false;

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            Log.d(TAG, "=============onPreferenceChange: " + value);

            if(preference instanceof ListPreference){
                if(preference.getKey().equals(preference.getContext().getString(R.string.setting_storage_scan_tree_key))){
                    Log.d(TAG, "onPreferenceChange: " + value);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                    if((value).equals(sharedPref.getString(preference.getContext().getString(R.string.setting_storage_scan_tree_key), D.DEFAULT_TREE_FILE))){
                        isLoadChanged = true;
                    }

                }
            }

//            if (preference instanceof ListPreference) {
//            String stringValue = value.toString();
//                // For list preferences, look up the correct display value in
//                // the preference's 'entries' list.
//                ListPreference listPreference = (ListPreference) preference;
//                int index = listPreference.findIndexOfValue(stringValue);
//
//                // Set the summary to reflect the new value.
//                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
//
//            } else {
//                // For all other preferences, set the summary to the value's
//                // simple string representation.
//                preference.setSummary(stringValue);
//            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if(preference instanceof SwitchPreference){

            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean(preference.getKey(), false));
        }else{

            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        getFragmentManager().beginTransaction()
                .replace(R.id.setting_content, new VideoPreferenceFragment())
                .commit();
        /**重置设置的记录**/
        isLoadChanged = false;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class VideoPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_video_setting);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.setting_storage_scan_tree_key)));
//            bindPreferenceSummaryToValue(findPreference("video_switch"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {

                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isLoadChanged) {
            SyncService.startActionClearSql(this);
            SyncService.startActionCheck(this);
            SyncService.startActionTraversal(this);
        }
    }
}