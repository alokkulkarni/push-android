package io.pivotal.android.push.model.events;

import io.pivotal.android.analytics.model.events.Event;

public class EventPushRegistered {

    public static final String EVENT_TYPE = "event_push_registered";

    public static Event getEvent(String variantUuid, String deviceId) {
        return PushEventHelper.getEvent(EVENT_TYPE, variantUuid, deviceId);
    }
}