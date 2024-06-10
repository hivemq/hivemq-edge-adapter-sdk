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
package com.hivemq.adapter.sdk.api.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for a unique type identifier used by Edge to reference various entities, e.g. bridges or adapters.
 */
public interface TypeIdentifier {

    enum Type {
        BRIDGE, ADAPTER, ADAPTER_TYPE, EVENT, USER
    }

    /**
     * @return the type of entity of this identifier
     */
    @JsonProperty("type")
    @Schema(description = "The type of the associated object/entity",
            required = true)
    @NotNull Type getType();

    /**
     * @return a string representing the unique id
     */
    @JsonProperty("identifier")
    @Schema(description = "The identifier associated with the object, a combination of type and identifier is used to uniquely identify an object in the system")
    @NotNull String getIdentifier();

    /**
     * @return composition of type and identifier
     */
    @NotNull String getFullQualifiedIdentifier();

}
