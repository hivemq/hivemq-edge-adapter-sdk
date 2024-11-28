/*
 * Copyright 2019-present HiveMQ GmbH
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
package com.hivemq.adapter.sdk.api.mappings.fromedge;

import com.hivemq.adapter.sdk.api.config.MessageHandlingOptions;
import com.hivemq.adapter.sdk.api.config.MqttUserProperty;
import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.adapter.sdk.api.mappings.fields.FieldMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class FromEdgeMapping implements PollingContext {

    private final @NotNull String topic;
    private final @NotNull String tagName;
    private final @NotNull MessageHandlingOptions messageHandlingOptions;
    private final boolean includeTagNames;
    private final boolean includeTimestamp;
    private final @NotNull List<MqttUserProperty> userProperties;
    private final int maxQoS;
    private final long messageExpiryInterval;
    private final @Nullable FieldMapping fieldMapping;

    public FromEdgeMapping(
            final @NotNull String tagName,
            final @NotNull String topic,
            final int maxQoS,
            final long messageExpiryInterval,
            final @NotNull MessageHandlingOptions messageHandlingOptions,
            final boolean includeTagNames,
            final boolean includeTimestamp,
            final @NotNull List<MqttUserProperty> userProperties,
            final @Nullable FieldMapping fieldMapping) {
        this.tagName = tagName;
        this.topic = topic;
        this.messageHandlingOptions = messageHandlingOptions;
        this.messageExpiryInterval = messageExpiryInterval;
        this.includeTagNames = includeTagNames;
        this.includeTimestamp = includeTimestamp;
        this.userProperties = userProperties;
        this.maxQoS = maxQoS;
        this.fieldMapping = fieldMapping;
    }

    @Override
    public @NotNull String getMqttTopic() {
        return topic;
    }

    @Override
    public @NotNull String getTagName() {
        return tagName;
    }

    @Override
    public int getMqttQos() {
        return maxQoS;
    }

    @Override
    public @NotNull MessageHandlingOptions getMessageHandlingOptions() {
        return messageHandlingOptions;
    }

    @Override
    public @NotNull Boolean getIncludeTimestamp() {
        return includeTimestamp;
    }

    @Override
    public @NotNull Boolean getIncludeTagNames() {
        return includeTagNames;
    }

    @Override
    public @NotNull List<MqttUserProperty> getUserProperties() {
        return userProperties;
    }

    @Override
    public @Nullable Long getMessageExpiryInterval() {
        return this.messageExpiryInterval;
    }

    @Override
    public @Nullable FieldMapping getFieldMapping() {
        return fieldMapping;
    }

    public static @NotNull FromEdgeMapping from(final @NotNull PollingContext fromEdgeMapping) {
        final List<MqttUserProperty> mqttUserPropertyEntities = fromEdgeMapping.getUserProperties()
                .stream()
                .map(mqttUserProperty -> new MqttUserProperty(mqttUserProperty.getName(),
                        mqttUserProperty.getValue()))
                .collect(Collectors.toList());

        return new FromEdgeMapping(
                fromEdgeMapping.getTagName(),
                fromEdgeMapping.getMqttTopic(),
                fromEdgeMapping.getMqttQos(),
                fromEdgeMapping.getMessageExpiryInterval(),
                fromEdgeMapping.getMessageHandlingOptions(),
                fromEdgeMapping.getIncludeTagNames(),
                fromEdgeMapping.getIncludeTimestamp(),
                mqttUserPropertyEntities,
                fromEdgeMapping.getFieldMapping());
    }

}
