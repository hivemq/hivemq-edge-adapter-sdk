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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.annotations.ModuleConfigField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Class to model user properties in the {@link PollingContext} of protocol adapters.
 */
public class UserProperty {

    @JsonProperty("propertyName")
    @ModuleConfigField(title = "Property Name", description = "Name of the associated property")
    private @Nullable String propertyName = null;

    @JsonProperty("propertyValue")
    @ModuleConfigField(title = "Property Value", description = "Value of the associated property")
    private @Nullable String propertyValue = null;

    public UserProperty() {
    }

    public UserProperty(@Nullable final String propertyName, @Nullable final String propertyValue) {
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
     * @param propertyName the name for this user property. The same name may be used for multiple user properties of a MQTT publish.
     */
    public void setName(final @NotNull String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @return the value of this user property.
     */
    public @NotNull String getValue() {
        return propertyValue;
    }

    /**
     * @param propertyValue the value of the user property.
     */
    public void setValue(final @NotNull String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        final UserProperty that = (UserProperty) o;
        if (!Objects.equals(propertyName, that.propertyName)) {
            return false;
        }
        return Objects.equals(propertyValue, that.propertyValue);
    }

    @Override
    public int hashCode() {
        int result = propertyName != null ? propertyName.hashCode() : 0;
        result = 31 * result + (propertyValue != null ? propertyValue.hashCode() : 0);
        return result;
    }

    @Override
    public @NotNull String toString() {
        return "UserProperty{" +
                "propertyName='" + propertyName + '\'' +
                ", propertyValue='" + propertyValue + '\'' +
                '}';
    }
}
