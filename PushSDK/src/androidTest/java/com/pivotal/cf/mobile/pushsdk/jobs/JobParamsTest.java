package com.pivotal.cf.mobile.pushsdk.jobs;

import android.test.AndroidTestCase;

import com.pivotal.cf.mobile.pushsdk.backend.BackEndMessageReceiptApiRequestProvider;
import com.pivotal.cf.mobile.pushsdk.network.FakeNetworkWrapper;
import com.pivotal.cf.mobile.pushsdk.prefs.FakePreferencesProvider;
import com.pivotal.cf.mobile.pushsdk.backend.FakeBackEndMessageReceiptApiRequest;
import com.pivotal.cf.mobile.pushsdk.broadcastreceiver.FakeEventsSenderAlarmProvider;
import com.pivotal.cf.mobile.pushsdk.database.FakeEventsStorage;

public class JobParamsTest extends AndroidTestCase {

    private FakeEventsStorage eventsStorage;
    private FakeNetworkWrapper networkWrapper;
    private FakePreferencesProvider preferencesProvider;
    private FakeEventsSenderAlarmProvider alarmProvider;
    private FakeBackEndMessageReceiptApiRequest backEndMessageReceiptApiRequest;
    private BackEndMessageReceiptApiRequestProvider backEndMessageReceiptApiRequestProvider;
    private JobResultListener listener;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        eventsStorage = new FakeEventsStorage();
        networkWrapper = new FakeNetworkWrapper();
        alarmProvider = new FakeEventsSenderAlarmProvider();
        preferencesProvider = new FakePreferencesProvider(null, null, 0, null, null, null, null, null);
        backEndMessageReceiptApiRequest = new FakeBackEndMessageReceiptApiRequest();
        backEndMessageReceiptApiRequestProvider = new BackEndMessageReceiptApiRequestProvider(backEndMessageReceiptApiRequest);
        listener = new JobResultListener() {
            @Override
            public void onJobComplete(int resultCode) {
                fail();
            }
        };
    }

    public void testRequiresContext() {
        try {
            new JobParams(null, listener, networkWrapper, eventsStorage, preferencesProvider, alarmProvider, backEndMessageReceiptApiRequestProvider);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testRequiresListener() {
        try {
            new JobParams(getContext(), null, networkWrapper, eventsStorage, preferencesProvider, alarmProvider, backEndMessageReceiptApiRequestProvider);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testRequiresNetworkWrapper() {
        try {
            new JobParams(getContext(), listener, null, eventsStorage, preferencesProvider, alarmProvider, backEndMessageReceiptApiRequestProvider);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testRequiresEventsStorage() {
        try {
            new JobParams(getContext(), listener, networkWrapper, null, preferencesProvider, alarmProvider, backEndMessageReceiptApiRequestProvider);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testRequiresPreferencesProvider() {
        try {
            new JobParams(getContext(), listener, networkWrapper, eventsStorage, null, alarmProvider, backEndMessageReceiptApiRequestProvider);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testRequiresAlarmProvider() {
        try {
            new JobParams(getContext(), listener, networkWrapper, eventsStorage, preferencesProvider, null, backEndMessageReceiptApiRequestProvider);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testRequiresBackEndMessageReceiptApiRequestProvider() {
        try {
            new JobParams(getContext(), listener, networkWrapper, eventsStorage, preferencesProvider, alarmProvider, null);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }
}