/*
 * Copyright 2023-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.adapter.sdk.api.config;

import com.hivemq.adapter.sdk.api.data.JsonPayloadCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PollingContext {

    /**
     * @return the topic on which the data will be available
     */
    @NotNull
    String getMqttTopic();

    @NotNull
    String getTagName();

    /**
     * @return the Quality-of-Service for the MQTT publish containing the data
     */
    int getMqttQos();

    /**
     * @return how the data will be published (one publish for all tags, a separate publish for each tag)
     */
    @NotNull
    MessageHandlingOptions getMessageHandlingOptions();

    /**
     * @return whether the MQTT publish payload should contain the timestamp
     */
    @NotNull
    Boolean getIncludeTimestamp();

    /**
     * @return whether the MQTT publish payload should contain the tag names
     */
    @NotNull
    Boolean getIncludeTagNames();

    /**
     * @return a list of MQTT user properties that are added to the MQTT publish
     */
    @NotNull
    List<MqttUserProperty> getUserProperties();

    /**
     * @return optional custom implementation of the {@link JsonPayloadCreator} to overwrite the default creation of the
     *         mqtt payloads
     */
    default @Nullable JsonPayloadCreator getJsonPayloadCreator() {
        return null;
    }

    /**
     * @return optional custom implementation of the {@link JsonPayloadCreator} to overwrite the default creation of the
     *         mqtt payloads
     */
    default @Nullable Long getMessageExpiryInterval() {
        return Long.MAX_VALUE; //Default taken from com.hivemq.mqtt.message.publish.PUBLISH.MESSAGE_EXPIRY_INTERVAL_NOT_SET
    }

    default boolean publishChangedDataOnly(){
        return false;
    }
}
