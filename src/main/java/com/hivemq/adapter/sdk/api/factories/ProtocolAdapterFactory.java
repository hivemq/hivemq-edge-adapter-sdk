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
package com.hivemq.adapter.sdk.api.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.adapter.sdk.api.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.ProtocolAdapter2;
import com.hivemq.adapter.sdk.api.ProtocolAdapterInformation;
import com.hivemq.adapter.sdk.api.config.ProtocolSpecificAdapterConfig;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterInput;
import com.hivemq.adapter.sdk.api.services.ModuleServices;
import com.hivemq.adapter.sdk.api.tag.Tag;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * The factory is responsible for constructing and managing the lifecycle of the various aspects of the adapter
 * sub-systems. We bind this to the configuration types so we can provide tightly coupled implementation instances
 * responsible for adapter management.
 */
public interface ProtocolAdapterFactory<E extends ProtocolSpecificAdapterConfig> {

    /**
     * Returns metadata related to the protocol adapter instance, including descriptions, iconography, categorisation,
     * etc.
     *
     * @return the instance that provides the adapter information
     */
    @NotNull
    ProtocolAdapterInformation getInformation();

    /**
     * This method is called by HiveMQ Edge to create an instance of the adapter.
     *
     * @param adapterInformation the information on the adapter
     * @param input              wrapper object for various information for the adapter
     * @return the protocol adapter instance
     */
    @NotNull
    ProtocolAdapter createAdapter(
            @NotNull ProtocolAdapterInformation adapterInformation, @NotNull ProtocolAdapterInput<E> input);

    /**
     * @param objectMapper the object mapper that converts the map to the actual config
     * @param config       a map containing the configuration of the adapter
     * @return a parsed config object for this adapter
     */
    default @NotNull ProtocolSpecificAdapterConfig convertConfigObject(
            final @NotNull ObjectMapper objectMapper,
            final @NotNull Map<String, Object> config,
            final boolean writingEnabled) {
        if (writingEnabled) {
            return objectMapper.convertValue(config, getInformation().configurationClassNorthAndSouthbound());
        } else {
            return objectMapper.convertValue(config, getInformation().configurationClassNorthbound());
        }
    }

    /**
     * @param objectMapper the object mapper that converts the map to the actual tag
     * @param tagList      a list of maps where each entry is a tag
     * @return a parsed tag object for this adapter
     */
    default @NotNull List<? extends Tag> convertTagDefinitionObjects(
            final @NotNull ObjectMapper objectMapper, final @NotNull List<Map<String, Object>> tagList) {
        return tagList.stream()
                .map(tagMap -> objectMapper.convertValue(tagMap, getInformation().tagConfigurationClass()))
                .collect(Collectors.toList());
    }

    /**
     * @param objectMapper the object mapper that converts the map to the actual tag
     * @param tag          a map that is a tag
     * @return a parsed tag object for this adapter
     */
    default @NotNull Tag convertTagDefinitionObject(
            final @NotNull ObjectMapper objectMapper, final @NotNull Map<String, Object> tag) {
        return objectMapper.convertValue(tag, getInformation().tagConfigurationClass());
    }

    /**
     * @param objectMapper the object mapper that converts the actual config to a map
     * @param config       the config for this adapter
     * @return a map containing the configuration of the adapter
     */
    default @NotNull Map<String, Object> unconvertConfigObject(
            final @NotNull ObjectMapper objectMapper, final @NotNull ProtocolSpecificAdapterConfig config) {
        return objectMapper.convertValue(config, Map.class);
    }

    /**
     * Creates a {@link ProtocolAdapter2} instance wrapping the given legacy adapter.
     * <p>
     * Module factories can override this method to return module-specific implementations.
     * The default implementation returns an inline adapter that delegates to the legacy adapter through
     * {@link ProtocolAdapter2}'s default methods.
     *
     * @param protocolAdapter the legacy adapter instance created by {@link #createAdapter}
     * @param moduleServices  the per-module services for the adapter
     * @return the {@link ProtocolAdapter2} implementation for this adapter type
     */
    default @NotNull ProtocolAdapter2 createProtocolAdapter2(
            final @NotNull ProtocolAdapter protocolAdapter, final @NotNull ModuleServices moduleServices) {
        return new ProtocolAdapter2() {
            @Override
            public @NotNull ProtocolAdapter getLegacyAdapter() {
                return protocolAdapter;
            }

            @Override
            public @NotNull ModuleServices getModuleServices() {
                return moduleServices;
            }
        };
    }
}
