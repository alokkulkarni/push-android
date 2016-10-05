/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.push.gcm;

import android.content.Context;

import java.io.IOException;

import io.pivotal.android.push.util.Logger;

/**
 * API request for unregistering a device with the Google Cloud Messaging (GCM)
 */
public class GcmUnregistrationApiRequestImpl implements GcmUnregistrationApiRequest {

    private Context context;
    private GcmProvider gcmProvider;
    private GcmUnregistrationListener listener;

    public GcmUnregistrationApiRequestImpl(Context context, GcmProvider gcmProvider) {
        verifyArguments(context, gcmProvider);
        saveArguments(context, gcmProvider);
    }

    private void verifyArguments(Context context, GcmProvider gcmProvider) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
        if (gcmProvider == null) {
            throw new IllegalArgumentException("gcmProvider may not be null");
        }
    }

    private void saveArguments(Context context, GcmProvider gcmProvider) {
        this.context = context;
        this.gcmProvider = gcmProvider;
    }

    @Override
    public void startUnregistration(GcmUnregistrationListener listener) {
        verifyUnregistrationArguments(listener);
        saveUnregistrationArguments(listener);
        executeUnregistration();
    }

    private void verifyUnregistrationArguments(GcmUnregistrationListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener may not be null");
        }
    }

    private void saveUnregistrationArguments(GcmUnregistrationListener listener) {
        this.listener = listener;
    }

    private void executeUnregistration() {
        try {
            gcmProvider.unregister();
            Logger.i("Device unregistered with GCM.");
            listener.onGcmUnregistrationComplete();

        } catch (IOException ex) {
            Logger.ex("Error unregistering device with GCM:", ex);
            listener.onGcmUnregistrationFailed(ex.getLocalizedMessage());
        }
    }

    @Override
    public GcmUnregistrationApiRequest copy() {
        return new GcmUnregistrationApiRequestImpl(context, gcmProvider);
    }
}
