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

package org.omnia.pushsdk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.omnia.pushsdk.backend.BackEndMessageReceiptApiRequest;
import org.omnia.pushsdk.backend.BackEndMessageReceiptApiRequestImpl;
import org.omnia.pushsdk.backend.BackEndMessageReceiptApiRequestProvider;
import org.omnia.pushsdk.backend.BackEndMessageReceiptListener;
import org.omnia.pushsdk.network.NetworkWrapperImpl;
import org.omnia.pushsdk.prefs.PreferencesProvider;
import org.omnia.pushsdk.prefs.RealPreferencesProvider;
import org.omnia.pushsdk.sample.util.PushLibLogger;

import java.util.concurrent.Semaphore;

public class GcmIntentService extends IntentService {

    public static final String BROADCAST_NAME_SUFFIX = ".omniapushsdk.RECEIVE_PUSH";
    public static final String KEY_RESULT_RECEIVER = "result_receiver";
    public static final String KEY_GCM_INTENT = "gcm_intent";
    public static final String KEY_MESSAGE_UUID = "message_uuid";

    public static final int NO_RESULT = -1;
    public static final int RESULT_EMPTY_INTENT = 100;
    public static final int RESULT_EMPTY_PACKAGE_NAME = 101;
    public static final int RESULT_NOTIFIED_APPLICATION = 102;

    // Used by unit tests
    /* package */ static Semaphore semaphore = null;
    /* package */ static PreferencesProvider preferencesProvider = null;
    /* package */ static BackEndMessageReceiptApiRequestProvider backEndMessageReceiptApiRequestProvider = null;

    private ResultReceiver resultReceiver = null;

    // TODO - write unit tests to cover this class

    public GcmIntentService() {
        super("GcmIntentService");
        if (GcmIntentService.preferencesProvider == null) {
            GcmIntentService.preferencesProvider = new RealPreferencesProvider(this);
        }
        if (GcmIntentService.backEndMessageReceiptApiRequestProvider == null) {
            final NetworkWrapperImpl networkWrapper = new NetworkWrapperImpl();
            final BackEndMessageReceiptApiRequestImpl dummyRequest = new BackEndMessageReceiptApiRequestImpl(networkWrapper);
            GcmIntentService.backEndMessageReceiptApiRequestProvider = new BackEndMessageReceiptApiRequestProvider(dummyRequest);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            doHandleIntent(intent);

        } finally {

            // Release the wake lock provided by the WakefulBroadcastReceiver.
            if (intent != null) {
                GcmBroadcastReceiver.completeWakefulIntent(intent);
            }

            // If unit tests are running then release them so that they can continue
            if (GcmIntentService.semaphore != null) {
                GcmIntentService.semaphore.release();
            }
        }
    }

    private void doHandleIntent(Intent intent) {

        if (intent == null) {
            return;
        }

        if (hasMessageUuid(intent)) {
            sendReturnReceipt(intent);
        }

        getResultReceiver(intent);

        if (isBundleEmpty(intent)) {
            PushLibLogger.i("Received message with no content.");
            sendResult(RESULT_EMPTY_INTENT);
        } else {
            notifyApplication(intent);
        }
    }

    private boolean hasMessageUuid(Intent intent) {
        return intent.hasExtra(KEY_MESSAGE_UUID);
    }

    private void sendReturnReceipt(Intent intent) {
        // TODO queue these receipts to send later to limit the number of server requests
        final String messageUuid = intent.getStringExtra(KEY_MESSAGE_UUID);
        final BackEndMessageReceiptApiRequest request = GcmIntentService.backEndMessageReceiptApiRequestProvider.getRequest();
        request.startMessageReceipt(messageUuid, new BackEndMessageReceiptListener() {

            @Override
            public void onBackEndMessageReceiptSuccess() {
                PushLibLogger.d("Sent message receipt successfully for msg_uuid \"" + messageUuid + "\".");
            }

            @Override
            public void onBackEndMessageReceiptFailed(String reason) {
                PushLibLogger.e("Got error trying to send message receipt for msg_uuid \"" + messageUuid + "\". Error: " + reason);
                // TODO - save for later?
            }
        });
    }

    private void getResultReceiver(Intent intent) {
        if (intent.hasExtra(KEY_RESULT_RECEIVER)) {
            // Used by unit tests
            resultReceiver = intent.getParcelableExtra(KEY_RESULT_RECEIVER);
            intent.removeExtra(KEY_RESULT_RECEIVER);
        }
    }

    private boolean isBundleEmpty(Intent intent) {
        final Bundle extras = intent.getExtras();
        return (extras == null || extras.size() <= 0);
    }

    private void notifyApplication(Intent gcmIntent) {
        final String broadcastName = getBroadcastName();
        if (broadcastName != null) {
            final Intent intent = new Intent(broadcastName);
            intent.putExtra(KEY_GCM_INTENT, gcmIntent);
            sendBroadcast(intent);
            sendResult(RESULT_NOTIFIED_APPLICATION);
        } else {
            sendResult(RESULT_EMPTY_PACKAGE_NAME);
        }
    }

    private String getBroadcastName() {
        final String packageName = preferencesProvider.loadPackageName();
        if (packageName == null) {
            return null;
        } else {
            final String broadcastName = packageName + BROADCAST_NAME_SUFFIX;
            return broadcastName;
        }
    }

    private void sendResult(int resultCode) {
        if (resultReceiver != null) {
            // Used by unit tests
            resultReceiver.send(resultCode, null);
        }
    }
}
