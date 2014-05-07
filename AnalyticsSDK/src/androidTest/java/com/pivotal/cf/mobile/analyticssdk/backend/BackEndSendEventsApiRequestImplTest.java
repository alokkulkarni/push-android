package com.pivotal.cf.mobile.analyticssdk.backend;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.pivotal.cf.mobile.analyticssdk.database.FakeEventsStorage;
import com.pivotal.cf.mobile.analyticssdk.model.events.EventTest;
import com.pivotal.cf.mobile.common.test.prefs.FakeAnalyticsPreferencesProvider;
import com.pivotal.cf.mobile.common.network.NetworkWrapper;
import com.pivotal.cf.mobile.common.test.network.FakeHttpURLConnection;
import com.pivotal.cf.mobile.common.test.network.FakeNetworkWrapper;
import com.pivotal.cf.mobile.common.test.util.DelayedLoop;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class BackEndSendEventsApiRequestImplTest extends AndroidTestCase {

    private static final String TEST_MESSAGE_UUID = "TEST-MESSAGE-UUID";
    private NetworkWrapper networkWrapper;
    private BackEndSendEventsListener backEndSendEventsListener;
    private DelayedLoop delayedLoop;
    private FakeEventsStorage eventsStorage;
    private FakeAnalyticsPreferencesProvider preferencesProvider;
    private static final long TEN_SECOND_TIMEOUT = 10000L;

    private List<Uri> emptyList;
    private List<Uri> listWithOneItem;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        eventsStorage = new FakeEventsStorage();
        preferencesProvider = new FakeAnalyticsPreferencesProvider(false, new URL("http://some/fake/host"));
        networkWrapper = new FakeNetworkWrapper();
        delayedLoop = new DelayedLoop(TEN_SECOND_TIMEOUT);
        FakeHttpURLConnection.reset();
        emptyList = new LinkedList<Uri>();
        listWithOneItem = new LinkedList<Uri>();
        final Uri uri = eventsStorage.saveEvent(EventTest.getEvent1());
        listWithOneItem.add(uri);
    }

    public void testRequiresContext() {
        try {
            new BackEndSendEventsApiRequestImpl(null, eventsStorage, preferencesProvider, networkWrapper);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresEventsStorage() {
        try {
            new BackEndSendEventsApiRequestImpl(getContext(), null, preferencesProvider, networkWrapper);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresPreferencesProvider() {
        try {
            new BackEndSendEventsApiRequestImpl(getContext(), eventsStorage, null, networkWrapper);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresNetworkWrapper() {
        try {
            new BackEndSendEventsApiRequestImpl(getContext(), eventsStorage, preferencesProvider, null);
            fail("Should not have succeeded");
        } catch (IllegalArgumentException ex) {
            // Success
        }
    }

    public void testRequiresMessageReceipts() {
        try {
            final BackEndSendEventsApiRequestImpl request = new BackEndSendEventsApiRequestImpl(getContext(), eventsStorage, preferencesProvider, networkWrapper);
            makeBackEndMessageReceiptListener(true);
            request.startSendEvents(null, backEndSendEventsListener);
            fail("Should not have succeeded");
        } catch (Exception e) {
            // Success
        }
    }

    public void testMessageReceiptsMayNotBeEmpty() {
        try {
            final BackEndSendEventsApiRequestImpl request = new BackEndSendEventsApiRequestImpl(getContext(), eventsStorage, preferencesProvider, networkWrapper);
            makeBackEndMessageReceiptListener(true);
            request.startSendEvents(emptyList, backEndSendEventsListener);
            fail("Should not have succeeded");
        } catch (Exception e) {
            // Success
        }
    }

    public void testRequiresListener() {
        try {
            final BackEndSendEventsApiRequestImpl request = new BackEndSendEventsApiRequestImpl(getContext(), eventsStorage, preferencesProvider, networkWrapper);
            request.startSendEvents(listWithOneItem, null);
            fail("Should not have succeeded");
        } catch (Exception e) {
            // Success
        }
    }

    public void testSuccessfulRequest() {
        makeListenersForSuccessfulRequestFromNetwork(true, 200);
        final BackEndSendEventsApiRequestImpl request = new BackEndSendEventsApiRequestImpl(getContext(), eventsStorage, preferencesProvider, networkWrapper);
        request.startSendEvents(listWithOneItem, backEndSendEventsListener);
        delayedLoop.startLoop();
        assertTrue(delayedLoop.isSuccess());
    }

    // TODO - restore test
//    public void testCouldNotConnect() {
//        makeListenersFromFailedRequestFromNetwork("Your server is busted", 0);
//        final BackEndMessageReceiptApiRequestImpl request = new BackEndMessageReceiptApiRequestImpl(networkWrapper);
//        request.startSendEvents(listWithOneItem, backEndMessageReceiptListener);
//        delayedLoop.startLoop();
//        assertTrue(delayedLoop.isSuccess());
//    }

    // TODO - restore test
//    public void testSuccessful400() {
//        makeListenersForSuccessfulRequestFromNetwork(false, 400);
//        final BackEndMessageReceiptApiRequestImpl request = new BackEndMessageReceiptApiRequestImpl(networkWrapper);
//        request.startSendEvents(listWithOneItem, backEndMessageReceiptListener);
//        delayedLoop.startLoop();
//        assertTrue(delayedLoop.isSuccess());
//    }

//    private void makeListenersFromFailedRequestFromNetwork(String exceptionText, int expectedHttpStatusCode) {
//        IOException exception = null;
//        if (exceptionText != null) {
//            exception = new IOException(exceptionText);
//        }
//        FakeHttpURLConnection.setConnectionException(exception);
//        FakeHttpURLConnection.willThrowConnectionException(true);
//        FakeHttpURLConnection.setResponseCode(expectedHttpStatusCode);
//        makeBackEndMessageReceiptListener(false);
//    }

    private void makeListenersForSuccessfulRequestFromNetwork(boolean isSuccessful, int expectedHttpStatusCode) {
        FakeHttpURLConnection.setResponseCode(expectedHttpStatusCode);
        makeBackEndMessageReceiptListener(isSuccessful);
    }

    private void makeBackEndMessageReceiptListener(final boolean isSuccessfulRequest) {
        backEndSendEventsListener = new BackEndSendEventsListener() {

            @Override
            public void onBackEndSendEventsSuccess() {
                assertTrue(isSuccessfulRequest);
                if (isSuccessfulRequest) {
                    delayedLoop.flagSuccess();
                } else {
                    delayedLoop.flagFailure();
                }
            }

            @Override
            public void onBackEndSendEventsFailed(String reason) {
                assertFalse(isSuccessfulRequest);
                if (isSuccessfulRequest) {
                    delayedLoop.flagFailure();
                } else {
                    delayedLoop.flagSuccess();
                }
            }
        };
    }
}