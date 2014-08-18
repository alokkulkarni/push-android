/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.push.prefs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.HashSet;
import java.util.Set;

/**
 * Saves preferences to the SharedPreferences on the filesystem.
 */
public class PushPreferencesProviderImpl implements PushPreferencesProvider {

    public static final String TAG_NAME = "PivotalMSSPush";

    // If you add or change any of these strings, then please also update their copies in the
    // sample app's MainActivity::clearRegistration method.
    private static final String PROPERTY_GCM_DEVICE_REGISTRATION_ID = "gcm_device_registration_id";
    private static final String PROPERTY_BACKEND_DEVICE_REGISTRATION_ID = "backend_device_registration_id";
    private static final String PROPERTY_APP_VERSION = "app_version";
    private static final String PROPERTY_GCM_SENDER_ID = "gcm_sender_id";
    private static final String PROPERTY_VARIANT_UUID = "variant_uuid";
    private static final String PROPERTY_VARIANT_SECRET = "variant_secret";
    private static final String PROPERTY_DEVICE_ALIAS = "device_alias";
    private static final String PROPERTY_PACKAGE_NAME = "package_name";
    private static final String PROPERTY_BASE_SERVER_URL = "base_server_url";
    private static final String PROPERTY_TAGS = "tags";

    private final Context context;

    public PushPreferencesProviderImpl(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
        this.context = context;
    }

    @Override
    public String getGcmDeviceRegistrationId() {
        return getSharedPreferences().getString(PROPERTY_GCM_DEVICE_REGISTRATION_ID, null);
    }

    @Override
    public void setGcmDeviceRegistrationId(String gcmDeviceRegistrationId) {
        final SharedPreferences prefs = getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_GCM_DEVICE_REGISTRATION_ID, gcmDeviceRegistrationId);
        editor.commit();
    }

    @Override
    public String getBackEndDeviceRegistrationId() {
        return getSharedPreferences().getString(PROPERTY_BACKEND_DEVICE_REGISTRATION_ID, null);
    }

    @Override
    public void setBackEndDeviceRegistrationId(String backendDeviceRegistrationId) {
        final SharedPreferences prefs = getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_BACKEND_DEVICE_REGISTRATION_ID, backendDeviceRegistrationId);
        editor.commit();
    }

    @Override
    public int getAppVersion() {
        return getSharedPreferences().getInt(PROPERTY_APP_VERSION, NO_SAVED_VERSION);
    }

    @Override
    public void setAppVersion(int appVersion) {
        final SharedPreferences prefs = getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    @Override
    public String getGcmSenderId() {
        return getSharedPreferences().getString(PROPERTY_GCM_SENDER_ID, null);
    }

    @Override
    public void setGcmSenderId(String gcmSenderId) {
        final SharedPreferences prefs = getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_GCM_SENDER_ID, gcmSenderId);
        editor.commit();
    }

    @Override
    public String getVariantUuid() {
        return getSharedPreferences().getString(PROPERTY_VARIANT_UUID, null);
    }

    @Override
    public void setVariantUuid(String variantUuid) {
        final SharedPreferences prefs = getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_VARIANT_UUID, variantUuid);
        editor.commit();
    }

    @Override
    public String getVariantSecret() {
        return getSharedPreferences().getString(PROPERTY_VARIANT_SECRET, null);
    }

    @Override
    public void setVariantSecret(String variantUuid) {
        final SharedPreferences prefs = getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_VARIANT_SECRET, variantUuid);
        editor.commit();
    }

    @Override
    public String getDeviceAlias() {
        return getSharedPreferences().getString(PROPERTY_DEVICE_ALIAS, null);
    }

    @Override
    public void setDeviceAlias(String deviceAlias) {
        final SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_DEVICE_ALIAS, deviceAlias);
        editor.commit();
    }

    @Override
    public String getPackageName() {
        return getSharedPreferences().getString(PROPERTY_PACKAGE_NAME, null);
    }

    @Override
    public void setPackageName(String packageName) {
        final SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_PACKAGE_NAME, packageName);
        editor.commit();
    }

    @Override
    public String getBaseServerUrl() {
        return getSharedPreferences().getString(PROPERTY_BASE_SERVER_URL, null);
    }

    @Override
    public void setBaseServerUrl(String baseServerUrl) {
        final SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_BASE_SERVER_URL, baseServerUrl);
        editor.commit();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getTags() {
        return getSharedPreferences().getStringSet(PROPERTY_TAGS, new HashSet<String>());
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(TAG_NAME, Context.MODE_PRIVATE);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setTags(Set<String> tags) {
        final SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(PROPERTY_TAGS, tags);
        editor.commit();
    }
}
