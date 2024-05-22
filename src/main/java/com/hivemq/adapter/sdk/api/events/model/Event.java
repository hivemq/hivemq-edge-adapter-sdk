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
package com.hivemq.adapter.sdk.api.events.model;

import com.hivemq.adapter.sdk.api.events.EventService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for events for the {@link EventService}.
 */
public interface Event {

    /**
     * @return the severity of the event
     */
    @NotNull SEVERITY getSeverity();

    /**
     * @return the message of this event
     */
    @NotNull String getMessage();

    /**
     * @return the {@link Payload} associated with this event
     */
    @Nullable Payload getPayload();

    /**
     * @return unix timestamp when this event was created
     */
    @NotNull Long getTimestamp();

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
     * @return The system-wide identifier of the object
     */
    @NotNull TypeIdentifier getIdentifier();

    enum SEVERITY {
        INFO,
        WARN,
        ERROR,
        CRITICAL
    }
}
