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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.annotations.ModuleConfigField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Class to model MQTT user properties in the {@link PollingContext} of protocol adapters.
 */
public class MqttUserProperty {

    @JsonProperty(value = "name", required = true)
    @ModuleConfigField(title = "Name", description = "Name of the associated property", required = true)
    private final @NotNull String name;

    @JsonProperty(value = "value", required = true)
    @ModuleConfigField(title = "Value", description = "Value of the associated property", required = true)
    private final @NotNull String value;

    @JsonCreator
    public MqttUserProperty(@JsonProperty(value = "name", required = true) final @NotNull String name,
                            @JsonProperty(value = "value", required = true) final @NotNull String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name for this user property.
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * @return the value of this user property.
     */
    public @NotNull String getValue() {
        return value;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MqttUserProperty that = (MqttUserProperty) o;
        if (!Objects.equals(name, that.name)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public @NotNull String toString() {
        return "MqttUserProperty{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
