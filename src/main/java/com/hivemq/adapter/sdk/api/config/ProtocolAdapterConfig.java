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

import com.hivemq.adapter.sdk.api.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for the configuration of Protocol Adapters.
 */

public interface ProtocolAdapterConfig {

    /**
     * Unique identifier for this protocol adapter instance.
     * <p>
     * Can only contain a-z, A-Z, 0-9, hyphen and underscore.
     *
     * @return the identifier
     */
    @NotNull
    String getId();


    @NotNull
    List<String> calculateAllUsedTags();

    /**
     * Get all tags defined for this protocol adapter instance.
     *
     * @return List of tag definitions owned by this protocol adapter instance
     */
    List<? extends Tag> getTags();

}
