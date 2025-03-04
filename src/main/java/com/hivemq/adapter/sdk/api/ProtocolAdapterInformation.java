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
package com.hivemq.adapter.sdk.api;


import com.hivemq.adapter.sdk.api.config.ProtocolSpecificAdapterConfig;
import com.hivemq.adapter.sdk.api.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

/**
 * A metadata object that describes the Adapter type into the platform. Will also give an indication
 * to the features and allows it to be categorized in the UI / API.
 */
public interface ProtocolAdapterInformation {

    /**
     * @return the technically correct protocol name as defined by the standard, for example "Http" or "Mqtt".
     */
    @NotNull
    String getProtocolName();

    /**
     * @return Protocol ID that will be used by the platform to group types, search and categories.
     *         NOTE: The format of this ID is important, it must be alpha-numeric without spaces and unique
     *         within the system.
     */
    @NotNull
    String getProtocolId();

    /**
     * @return the legacy Protocol Ids for older versions of this adapter
     */
    default @NotNull List<String> getLegacyProtocolIds() {
        return List.of();
    }

    /**
     * @return The visual name to display in the protocol adapter catalog for example "HTTP(s) to MQTT Protocol Adapter"
     */
    @NotNull
    String getDisplayName();

    @NotNull
    String getDescription();

    @NotNull
    String getUrl();

    @NotNull
    String getVersion();

    @NotNull
    String getLogoUrl();

    /**
     * The entity (person or company) who is responsible for producing the adapter
     *
     * @return the name of the authoring entity
     */
    @NotNull
    String getAuthor();

    /**
     * An adapter can be in a single category. This helps discovery purposes
     *
     * @return the category in which the adapter resides
     */
    @Nullable
    ProtocolAdapterCategory getCategory();

    /**
     * Tag represents the keywords that can be associated with this type of adapter
     *
     * @return a list of associated tags that can be used for search purposes
     */
    @Nullable
    List<ProtocolAdapterTag> getTags();


    /**
     * A bean class that will be reflected upon by the framework to determine the structural requirements of the
     * tag configuration associated with an adapter instance. It is expected that the bean class supplied, be marked up
     * with Jackson annotation.
     *
     * @return The class that represents (and will encapsulate) the configuration requirements of the adapter's tags
     */
    @NotNull
    Class<? extends Tag> tagConfigurationClass();

    /**
     * A bean class that will be reflected upon by the framework to determine the structural requirements of the
     * configuration associated with an adapter instance, in this case for when writing is disabled for this adapter.
     * It is expected that the bean class supplied, be marked up with Jackson annotation.
     *
     * @return The class that represents (and will encapsulate) the configuration requirements of the adapter
     *         \\@ModuleConfigField annotations.
     */
    @NotNull
    Class<? extends ProtocolSpecificAdapterConfig> configurationClassNorthbound();

    /**
     * A bean class that will be reflected upon by the framework to determine the structural requirements of the
     * configuration associated with an adapter instance, in this case for when writing is enabled for this adapter.
     * It is expected that the bean class supplied, be marked up
     * with Jackson annotation.
     *
     * @return The class that represents (and will encapsulate) the configuration requirements of the adapter
     *         \\@ModuleConfigField annotations.
     */
    @NotNull
    Class<? extends ProtocolSpecificAdapterConfig> configurationClassNorthAndSouthbound();


    /**
     * Get the capabilities associated with the adapter. For more information on capabilities, please refer to the
     * {@link ProtocolAdapterCapability} descriptions.
     *
     * @return an {@link EnumSet} containing the capabilities of the adapter
     */
    default @NotNull EnumSet<ProtocolAdapterCapability> getCapabilities() {
        return EnumSet.of(ProtocolAdapterCapability.READ, ProtocolAdapterCapability.DISCOVER);
    }

    /**
     * @return a UI Schema that tells Edge how to render the config in the UI.
     */
    default @Nullable String getUiSchema() {
        return null;
    }

    /**
     * @return the current version of the configuration
     */
    int getCurrentConfigVersion();
}
