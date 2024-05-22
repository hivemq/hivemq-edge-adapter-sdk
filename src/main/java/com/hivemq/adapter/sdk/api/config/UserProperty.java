/*
 * Copyright 2024-present HiveMQ GmbH
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

import org.jetbrains.annotations.NotNull;

/**
 * Interface for parsing mqtt user properties in adapter configurations.
 * The implementation in the HiveMQ Edge code will be used to automatically create instances during conversion:
 * <p>
 *     \\@JsonProperty("propertyName")
 *     \\@ModuleConfigField(title = "Property Name", description = "Name of the associated property")
 *     private @Nullable String propertyName = null;
 * <p>
 *     \\@JsonProperty("propertyValue")
 *     \\@ModuleConfigField(title = "Property Value", description = "Value of the associated property")
 *     private @Nullable String propertyValue = null;
 */
public interface UserProperty {
    /**
     * @return the name for this user property.
     */
    @NotNull String getName();

    /**
     *
     * @param propertyName the name for this user property. The same name may be used for multiple user properties of a MQTT publish.
     */
    void setName(@NotNull String propertyName);

    /**
     * @return the value of this user property.
     */
    @NotNull String getValue();

    /**
     * @param propertyValue the value of the user property.
     */
    void setValue(@NotNull String propertyValue);
}
