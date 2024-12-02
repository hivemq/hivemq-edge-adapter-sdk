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
package com.hivemq.adapter.sdk.api.config.legacy;

import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.adapter.sdk.api.config.ProtocolSpecificAdapterConfig;
import com.hivemq.adapter.sdk.api.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This represents the conversion result of a legacy config into the current state.
 *
 * @deprecated this class will be removed in early 2025, there won't be a replacement.
 */
@Deprecated()
public class ConfigTagsTuple {

    private final @NotNull String id;
    private final @NotNull ProtocolSpecificAdapterConfig config;
    private final @NotNull List<? extends Tag> tags;
    private final @NotNull List<? extends PollingContext> pollingContexts;


    public ConfigTagsTuple(
            final @NotNull String id,
            final @NotNull ProtocolSpecificAdapterConfig config,
            final @NotNull List<? extends Tag> tags,
            final @NotNull List<? extends PollingContext> pollingContexts) {
        this.id = id;
        this.config = config;
        this.tags = tags;
        this.pollingContexts = pollingContexts;
    }

    public @NotNull String getAdapterId() {
        return id;
    }

    public @NotNull ProtocolSpecificAdapterConfig getConfig() {
        return config;
    }

    public @NotNull List<? extends Tag> getTags() {
        return tags;
    }

    public @NotNull List<? extends PollingContext> getPollingContexts() {
        return pollingContexts;
    }
}
