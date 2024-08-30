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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.annotations.ModuleConfigField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Class to model user properties in the {@link PollingContext} of protocol adapters.
 */
public class MqttUserProperty {

    @JsonProperty("name")
    @JsonAlias("propertyName")
    @ModuleConfigField(title = "Name", description = "Name of the associated property", required = true)
    private final @NotNull String propertyName;

    @JsonProperty("value")
    @JsonAlias("propertyValue")
    @ModuleConfigField(title = "Value", description = "Value of the associated property", required = true)
    private final @NotNull String propertyValue;

    @JsonCreator
    public MqttUserProperty(@JsonProperty(value = "name", required = true) @NotNull final String propertyName,
                            @JsonProperty(value = "value", required = true) @NotNull final String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    /**
     * @return the name for this user property.
     */
    public @NotNull String getName() {
        return propertyName;
    }

    /**
     * @return the value of this user property.
     */
    public @NotNull String getValue() {
        return propertyValue;
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
        if (!Objects.equals(propertyName, that.propertyName)) {
            return false;
        }
        return Objects.equals(propertyValue, that.propertyValue);
    }

    @Override
    public int hashCode() {
        int result = propertyName.hashCode();
        result = 31 * result + propertyValue.hashCode();
        return result;
    }

    @Override
    public @NotNull String toString() {
        return "MqttUserProperty{" +
                "propertyName='" + propertyName + '\'' +
                ", propertyValue='" + propertyValue + '\'' +
                '}';
    }
}
