/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.push.backend;

import io.pivotal.android.push.RegistrationParameters;

public class FakeBackEndUnregisterDeviceApiRequest implements BackEndUnregisterDeviceApiRequest {

    private final FakeBackEndUnregisterDeviceApiRequest originatingRequest;
    private final boolean willBeSuccessfulRequest;
    private boolean wasUnregisterCalled = false;

    public FakeBackEndUnregisterDeviceApiRequest() {
        this.originatingRequest = null;
        this.willBeSuccessfulRequest = true;
    }

    public FakeBackEndUnregisterDeviceApiRequest(boolean willBeSuccessfulRequest) {
        this.originatingRequest = null;
        this.willBeSuccessfulRequest = willBeSuccessfulRequest;
    }

    public FakeBackEndUnregisterDeviceApiRequest(FakeBackEndUnregisterDeviceApiRequest originatingRequest, boolean willBeSuccessfulRequest) {
        this.originatingRequest = originatingRequest;
        this.willBeSuccessfulRequest = willBeSuccessfulRequest;
    }

    @Override
    public void startUnregisterDevice(String backEndDeviceRegistrationId, RegistrationParameters parameters, BackEndUnregisterDeviceListener listener) {
        wasUnregisterCalled = true;
        if (originatingRequest != null) {
            originatingRequest.wasUnregisterCalled = true;
        }

        if (willBeSuccessfulRequest) {
            listener.onBackEndUnregisterDeviceSuccess();
        } else {
            listener.onBackEndUnregisterDeviceFailed("Fake back-end registration failed fakely");
        }
    }

    @Override
    public BackEndUnregisterDeviceApiRequest copy() {
        return new FakeBackEndUnregisterDeviceApiRequest(this, willBeSuccessfulRequest);
    }

    public boolean wasUnregisterCalled() {
        return wasUnregisterCalled;
    }
}