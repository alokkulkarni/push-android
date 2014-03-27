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

package org.omnia.pushsdk.gcm;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.omnia.pushsdk.util.PushLibLogger;

import java.io.IOException;

public class RealGcmProvider implements GcmProvider {

    private GoogleCloudMessaging gcm;

    public RealGcmProvider(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
        gcm = GoogleCloudMessaging.getInstance(context);
    }

    @Override
    public String register(String... senderIds) throws IOException {
        return gcm.register(senderIds);
    }

    @Override
    public void unregister() throws IOException {
        gcm.unregister();
    }

    @Override
    public boolean isGooglePlayServicesInstalled(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            final String errorString = GooglePlayServicesUtil.getErrorString(resultCode);
            PushLibLogger.e("Google Play Services is not available: " + errorString);
            return false;
        }
        return true;
    }
}
