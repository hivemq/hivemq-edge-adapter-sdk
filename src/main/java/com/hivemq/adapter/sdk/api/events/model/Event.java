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
import com.hivemq.adapter.sdk.api.events.EventService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for events for the {@link EventService}.
 */
public interface Event {

    /**
     * @return the severity of the event
     */
    @JsonProperty("severity")
    @Schema(description = "The severity that this log is considered to be",
            required = true)
    @NotNull SEVERITY getSeverity();

    /**
     * @return the message of this event
     */
    @JsonProperty("message")
    @Schema(description = "The message associated with the event. A message will be no more than 1024 characters in length",
            type = "string",
            required = true)
    @NotNull String getMessage();

    /**
     * @return the {@link Payload} associated with this event
     */
    @JsonProperty("payload")
    @Schema(description = "Object to denote the payload of the event")
    @Nullable Payload getPayload();

    /**
     * @return unix timestamp when this event was created
     */
    @JsonProperty("timestamp")
    @Schema(description = "Time the event was generated in epoch format",
            required = true)
    @NotNull Long getTimestamp();

    @JsonProperty("created")
    @Schema(type = "string",
            format = "date-time",
            description = "Time the event was in date format",
            required = true)
    @NotNull Long getCreated();

    /**
     * @return the type identifier of the associated object
     */
    @Nullable TypeIdentifier getAssociatedObject();

    /**
     * @return the type identifier of the source of this event
     */
    @Nullable TypeIdentifier getSource();

    /**
     * Represents a uniquely identifiable object in the system.
     *
     * @return The system-wide identifier of the object
     */
    @JsonProperty("identifier")
    @Schema(description = "The unique id of the event object",
            required = true)
    @NotNull TypeIdentifier getIdentifier();

    enum SEVERITY {
        INFO,
        WARN,
        ERROR,
        CRITICAL
    }
}
