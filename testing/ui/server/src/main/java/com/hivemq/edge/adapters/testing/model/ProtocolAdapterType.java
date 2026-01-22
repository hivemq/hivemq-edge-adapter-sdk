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
package com.hivemq.edge.adapters.testing.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Represents a protocol adapter type as returned by the API.
 * This mirrors the structure expected by the HiveMQ Edge frontend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProtocolAdapterType {

    private final @NotNull String id;
    private final @NotNull String protocol;
    private final @NotNull String name;
    private final @Nullable String description;
    private final @Nullable String url;
    private final @Nullable String version;
    private final @Nullable String logoUrl;
    private final @Nullable String author;
    private final @Nullable String category;
    private final @Nullable List<String> tags;
    private final @Nullable Set<String> capabilities;
    private final @NotNull JsonNode configSchema;
    private final @Nullable JsonNode uiSchema;

    public ProtocolAdapterType(
            final @NotNull String id,
            final @NotNull String protocol,
            final @NotNull String name,
            final @Nullable String description,
            final @Nullable String url,
            final @Nullable String version,
            final @Nullable String logoUrl,
            final @Nullable String author,
            final @Nullable String category,
            final @Nullable List<String> tags,
            final @Nullable Set<String> capabilities,
            final @NotNull JsonNode configSchema,
            final @Nullable JsonNode uiSchema) {
        this.id = id;
        this.protocol = protocol;
        this.name = name;
        this.description = description;
        this.url = url;
        this.version = version;
        this.logoUrl = logoUrl;
        this.author = author;
        this.category = category;
        this.tags = tags;
        this.capabilities = capabilities;
        this.configSchema = configSchema;
        this.uiSchema = uiSchema;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull String getProtocol() {
        return protocol;
    }

    public @NotNull String getName() {
        return name;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public @Nullable String getUrl() {
        return url;
    }

    public @Nullable String getVersion() {
        return version;
    }

    public @Nullable String getLogoUrl() {
        return logoUrl;
    }

    public @Nullable String getAuthor() {
        return author;
    }

    public @Nullable String getCategory() {
        return category;
    }

    public @Nullable List<String> getTags() {
        return tags;
    }

    public @Nullable Set<String> getCapabilities() {
        return capabilities;
    }

    public @NotNull JsonNode getConfigSchema() {
        return configSchema;
    }

    public @Nullable JsonNode getUiSchema() {
        return uiSchema;
    }
}
