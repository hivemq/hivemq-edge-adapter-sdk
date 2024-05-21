package com.hivemq.adapter.sdk.api.config;

/**
 * Defines the handling of the messages created by the adapter.
 */
public enum MessageHandlingOptions {
    /**
     * A separate MQTT publish will be created per read tag.
     */
    MQTTMessagePerTag,

    /**
     * One MQTT publish will be created containing all read tags.
     */
    MQTTMessagePerSubscription
}
