/* Copyright (c) 2013 Pivotal Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pivotal.cf.mobile.pushsdk.sample.activity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pivotal.cf.mobile.pushsdk.sample.util.Settings;

public class SettingsActivity extends PreferenceActivity {

    private EditTextPreference gcmSenderIdPreference;
    private EditTextPreference variantUuidPreference;
    private EditTextPreference variantSecretPreference;
    private EditTextPreference deviceAliasPreference;
    private EditTextPreference gcmBrowserApiPreference;
    private EditTextPreference backEndEnvironmentUuidPreference;
    private EditTextPreference backEndEnvironmentKeyPreference;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        // NOTE - many of the method calls in this class show up as deprecated.  However, I still want my
        // app to run on old Android versions, so I'm going to leave them in here.
        addPreferencesFromResource(com.pivotal.cf.mobile.pushsdk.sample.R.xml.preferences);
        gcmSenderIdPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.GCM_SENDER_ID);
        variantUuidPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.VARIANT_UUID);
        variantSecretPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.VARIANT_SECRET);
        deviceAliasPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.DEVICE_ALIAS);
        gcmBrowserApiPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.GCM_BROWSER_API_KEY);
        backEndEnvironmentUuidPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.BACK_END_ENVIRONMENT_UUID);
        backEndEnvironmentKeyPreference = (EditTextPreference) getPreferenceScreen().findPreference(Settings.BACK_END_ENVIRONMENT_KEY);
        preferenceChangeListener = getPreferenceChangeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCurrentPreferences();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            final ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private SharedPreferences.OnSharedPreferenceChangeListener getPreferenceChangeListener() {
        return new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                showCurrentPreferences();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(com.pivotal.cf.mobile.pushsdk.sample.R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (id == com.pivotal.cf.mobile.pushsdk.sample.R.id.action_reset_preferences) {
            resetPreferencesToDefault();
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetPreferencesToDefault() {
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.edit().clear().commit();
        PreferenceManager.setDefaultValues(this, com.pivotal.cf.mobile.pushsdk.sample.R.xml.preferences, true);
        showCurrentPreferences();
    }

    private void showCurrentPreferences() {
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        gcmSenderIdPreference.setSummary(prefs.getString(Settings.GCM_SENDER_ID, null));
        variantUuidPreference.setSummary(prefs.getString(Settings.VARIANT_UUID, null));
        variantSecretPreference.setSummary(prefs.getString(Settings.VARIANT_SECRET, null));
        deviceAliasPreference.setSummary(prefs.getString(Settings.DEVICE_ALIAS, null));
        gcmBrowserApiPreference.setSummary(prefs.getString(Settings.GCM_BROWSER_API_KEY, null));
        backEndEnvironmentUuidPreference.setSummary(prefs.getString(Settings.BACK_END_ENVIRONMENT_UUID, null));
        backEndEnvironmentKeyPreference.setSummary(prefs.getString(Settings.BACK_END_ENVIRONMENT_KEY, null));
    }
}