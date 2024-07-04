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

}
