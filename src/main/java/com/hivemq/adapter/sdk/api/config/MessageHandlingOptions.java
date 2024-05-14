package com.hivemq.adapter.sdk.api.config;

public enum MessageHandlingOptions {
    /**
     * A separate publish will be created per tag
     */
    MQTTMessagePerTag,

    /**
     * One publish will be created for all tags
     */
    MQTTMessagePerSubscription
}
