package com.pivotal.cf.mobile.pushsdk.registration;

import android.test.AndroidTestCase;

import com.pivotal.cf.mobile.common.test.prefs.FakeAnalyticsPreferencesProvider;
import com.pivotal.cf.mobile.common.test.util.FakeServiceStarter;
import com.pivotal.cf.mobile.pushsdk.prefs.FakePushPreferencesProvider;
import com.pivotal.cf.mobile.common.util.Logger;
import com.pivotal.cf.mobile.pushsdk.RegistrationParameters;
import com.pivotal.cf.mobile.pushsdk.backend.BackEndUnregisterDeviceApiRequestProvider;
import com.pivotal.cf.mobile.pushsdk.backend.FakeBackEndUnregisterDeviceApiRequest;
import com.pivotal.cf.mobile.pushsdk.gcm.FakeGcmProvider;
import com.pivotal.cf.mobile.pushsdk.gcm.FakeGcmUnregistrationApiRequest;
import com.pivotal.cf.mobile.pushsdk.gcm.GcmUnregistrationApiRequestProvider;

import java.net.URL;
import java.util.concurrent.Semaphore;

public class UnregistrationEngineTest extends AndroidTestCase {

    private static final String TEST_GCM_DEVICE_REGISTRATION_ID_1 = "TEST_GCM_DEVICE_REGISTRATION_ID_1";
    private static final String TEST_BACK_END_DEVICE_REGISTRATION_ID_1 = "TEST_BACK_END_DEVICE_REGISTRATION_ID_1";
    private static final String TEST_GCM_SENDER_ID = "TEST_GCM_SENDER_ID";
    private static final String TEST_VARIANT_UUID = "TEST_VARIANT_UUID";
    private static final String TEST_VARIANT_SECRET = "TEST_VARIANT_SECRET";
    private static final String TEST_DEVICE_ALIAS = "TEST_DEVICE_ALIAS";
    private static final String TEST_BASE_SERVER_URL = "http://test.com";

    private FakeGcmProvider gcmProvider;
    private GcmUnregistrationApiRequestProvider gcmUnregistrationApiRequestProvider;
    private FakePushPreferencesProvider pushPreferencesProvider;
    private FakeAnalyticsPreferencesProvider analyticsPreferencesProvider;
    private BackEndUnregisterDeviceApiRequestProvider backEndUnregisterDeviceApiRequestProvider;
    private FakeServiceStarter serviceStarter;
    private RegistrationParameters parameters;
    private Semaphore semaphore = new Semaphore(0);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final URL url = new URL(TEST_BASE_SERVER_URL);
        parameters = new RegistrationParameters(TEST_GCM_SENDER_ID, TEST_VARIANT_UUID, TEST_VARIANT_SECRET, TEST_DEVICE_ALIAS, url);
        serviceStarter = new FakeServiceStarter();
        pushPreferencesProvider = new FakePushPreferencesProvider(null, null, 0, null, null, null, null, null, null);
        analyticsPreferencesProvider = new FakeAnalyticsPreferencesProvider(true, null);
        gcmProvider = new FakeGcmProvider(TEST_GCM_DEVICE_REGISTRATION_ID_1);
        gcmUnregistrationApiRequestProvider = new GcmUnregistrationApiRequestProvider(new FakeGcmUnregistrationApiRequest(gcmProvider));
        backEndUnregisterDeviceApiRequestProvider = new BackEndUnregisterDeviceApiRequestProvider(new FakeBackEndUnregisterDeviceApiRequest());
    }

    public void testNullContext() {
        try {
            new UnregistrationEngine(null, gcmProvider, serviceStarter, pushPreferencesProvider, analyticsPreferencesProvider, gcmUnregistrationApiRequestProvider, backEndUnregisterDeviceApiRequestProvider);
            fail("should not have succeeded");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNullGcmProvider() {
        try {
            new UnregistrationEngine(getContext(), null, serviceStarter, pushPreferencesProvider, analyticsPreferencesProvider, gcmUnregistrationApiRequestProvider, backEndUnregisterDeviceApiRequestProvider);
            fail("should not have succeeded");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNullPushPreferencesProvider() {
        try {
            new UnregistrationEngine(getContext(), gcmProvider, serviceStarter, null, analyticsPreferencesProvider, gcmUnregistrationApiRequestProvider, backEndUnregisterDeviceApiRequestProvider);
            fail("should not have succeeded");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNullAnalyticsPreferencesProvider() {
        try {
            new UnregistrationEngine(getContext(), gcmProvider, serviceStarter, pushPreferencesProvider, null, gcmUnregistrationApiRequestProvider, backEndUnregisterDeviceApiRequestProvider);
            fail("should not have succeeded");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNullServiceStarter() {
        try {
            new UnregistrationEngine(getContext(), gcmProvider, null, pushPreferencesProvider, analyticsPreferencesProvider, gcmUnregistrationApiRequestProvider, backEndUnregisterDeviceApiRequestProvider);
            fail("should not have succeeded");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNullGcmUnregistrationApiRequestProvider() {
        try {
            new UnregistrationEngine(getContext(), gcmProvider, serviceStarter, pushPreferencesProvider, analyticsPreferencesProvider, null, backEndUnregisterDeviceApiRequestProvider);
            fail("should not have succeeded");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNullBackEndApiUnregisterDeviceRequestProvider() {
        try {
            new UnregistrationEngine(getContext(), gcmProvider, serviceStarter, pushPreferencesProvider, analyticsPreferencesProvider, gcmUnregistrationApiRequestProvider, null);
            fail("should not have succeeded");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNullParameters() {
        try {
            final UnregistrationEngine engine = new UnregistrationEngine(getContext(), gcmProvider, serviceStarter, pushPreferencesProvider, analyticsPreferencesProvider, gcmUnregistrationApiRequestProvider, backEndUnregisterDeviceApiRequestProvider);
            engine.unregisterDevice(null, getListenerForUnregistration(false));
            fail("should not have succeeded");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testNullBaseServerUrl() {
        try {
            final UnregistrationEngine engine = new UnregistrationEngine(getContext(),gcmProvider, serviceStarter, pushPreferencesProvider, analyticsPreferencesProvider, gcmUnregistrationApiRequestProvider, backEndUnregisterDeviceApiRequestProvider);
            parameters = new RegistrationParameters(TEST_GCM_SENDER_ID, TEST_VARIANT_UUID, TEST_VARIANT_SECRET, TEST_DEVICE_ALIAS, null);
            engine.unregisterDevice(parameters, getListenerForUnregistration(false));
            fail("should not have succeeded");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testGooglePlayServicesNotAvailable() throws InterruptedException {
        gcmProvider.setIsGooglePlayServicesInstalled(false);
        final UnregistrationEngine engine = new UnregistrationEngine(getContext(), gcmProvider, serviceStarter, pushPreferencesProvider, analyticsPreferencesProvider, gcmUnregistrationApiRequestProvider, backEndUnregisterDeviceApiRequestProvider);
        engine.unregisterDevice(parameters, getListenerForUnregistration(false));
        semaphore.acquire();
    }

    public void testSuccessfulUnregistration() throws Exception {
        UnregistrationEngineTestParameters testParams = new UnregistrationEngineTestParameters(getContext())
                .setShouldGcmDeviceUnregistrationBeSuccessful(true)
                .setupBackEndDeviceRegistrationId(TEST_BACK_END_DEVICE_REGISTRATION_ID_1, null)
                .setupParameters(parameters)
                .setShouldUnregistrationHaveSucceeded(true);
        testParams.run();
    }

    public void testSuccessfulUnregistrationFromGcmOnly() throws Exception {
        UnregistrationEngineTestParameters testParams = new UnregistrationEngineTestParameters(getContext())
                .setShouldGcmDeviceUnregistrationBeSuccessful(true)
                .setupBackEndDeviceRegistrationId(null, null)
                .setupParameters(parameters)
                .setShouldUnregistrationHaveSucceeded(true);
        testParams.run();
    }

    public void testUnregistrationWhenGcmUnregistrationFails() throws Exception {
        UnregistrationEngineTestParameters testParams = new UnregistrationEngineTestParameters(getContext())
                .setShouldGcmDeviceUnregistrationBeSuccessful(false)
                .setupBackEndDeviceRegistrationId(TEST_BACK_END_DEVICE_REGISTRATION_ID_1, null)
                .setupParameters(parameters)
                .setShouldUnregistrationHaveSucceeded(true);
        testParams.run();
    }

    public void testUnregistrationWhenGcmUnregistrationFailsAndBackEndUnregistrationFails() throws Exception {
        UnregistrationEngineTestParameters testParams = new UnregistrationEngineTestParameters(getContext())
                .setShouldGcmDeviceUnregistrationBeSuccessful(false)
                .setupBackEndDeviceRegistrationId(TEST_BACK_END_DEVICE_REGISTRATION_ID_1, TEST_BACK_END_DEVICE_REGISTRATION_ID_1)
                .setupParameters(parameters)
                .setShouldUnregistrationHaveSucceeded(false);
        testParams.run();
    }

    public void testUnregistrationWhenGcmUnregistrationSucceedsAndBackEndUnregistrationFails() throws Exception {
        UnregistrationEngineTestParameters testParams = new UnregistrationEngineTestParameters(getContext())
                .setShouldGcmDeviceUnregistrationBeSuccessful(true)
                .setupBackEndDeviceRegistrationId(TEST_BACK_END_DEVICE_REGISTRATION_ID_1, TEST_BACK_END_DEVICE_REGISTRATION_ID_1)
                .setupParameters(parameters)
                .setShouldUnregistrationHaveSucceeded(false);
        testParams.run();
    }

    private UnregistrationListener getListenerForUnregistration(final boolean isSuccessfulUnregistration) {
        return new UnregistrationListener() {

            @Override
            public void onUnregistrationComplete() {
                assertTrue(isSuccessfulUnregistration);
                semaphore.release();
            }

            @Override
            public void onUnregistrationFailed(String reason) {
                if (isSuccessfulUnregistration) {
                    Logger.e("Test failed due to error:" + reason);
                }
                assertFalse(isSuccessfulUnregistration);
                semaphore.release();
            }
        };
    }
}
