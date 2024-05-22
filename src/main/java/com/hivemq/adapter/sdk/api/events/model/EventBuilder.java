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

/**
 * Builder interface to create {@link Event} for the {@link EventService} to notify HiveMQ Edge of any event regarding
 * components such as protocol adapters.
 */
public interface EventBuilder {
    /**
     * @param severity the severity of the event
     * @return the builder for a fluent api
     */
    @NotNull EventBuilder withSeverity(Event.@NotNull SEVERITY severity);

    /**
     * @param message the message of the event
     * @return the builder for a fluent api
     */
    @NotNull EventBuilder withMessage(@NotNull String message);

    /**
     * @param contentType the content type of the payload
     * @param content the content of the payload
     * @return the builder for a fluent api
     */
    @NotNull EventBuilder withPayload(final @NotNull Payload.ContentType contentType,
                                      final @NotNull String content);

    /**
     * @param data a Jackson annotated Object from which the object mapper creates a json from.
     * @return the builder for a fluent api
     */
    @NotNull EventBuilder withPayload( final @NotNull Object data);

    /**
     * @param timestamp the unix timestamp of this event
     * @return the builder for a fluent api
     */
    @NotNull EventBuilder withTimestamp(@NotNull Long timestamp);

    /**
     * @param associatedObject the type identifier of the associated object for this event
     * @return the builder for a fluent api
     */
    @NotNull EventBuilder withAssociatedObject(@NotNull TypeIdentifier associatedObject);

    /**
     * @param source the type identifier of the source of this evemt
     * @return the builder for a fluent api
     */
    @NotNull EventBuilder withSource(@NotNull TypeIdentifier source);

    /**
     * This method is to be called after populating the builder with information to fire this event via the EventService.
     */
    void fire();

}
