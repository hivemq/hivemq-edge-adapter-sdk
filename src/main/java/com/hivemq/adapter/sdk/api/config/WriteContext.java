package com.hivemq.adapter.sdk.api.config;

import org.jetbrains.annotations.Nullable;

public interface WriteContext {

    /**
     * @return the mqtt topic from which the data will be read
     */
    @Nullable
    String getSourceMqttTopic();


    /**
     * @return the Quality-of-Service for the MQTT publish containing the source data that will be written
     */
    int getQos();


    /**
     * The minimum time interval between writes. If this is 0 no interval between writes is guaranteed.
     * This interval is intended to protect constrained devices from an overload because of writes.
     *
     * @return long the interval between writes in ms.
     */
    long getPeriod();

}
