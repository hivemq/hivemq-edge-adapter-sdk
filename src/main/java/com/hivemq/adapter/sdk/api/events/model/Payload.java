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
 * Payload of an {@link Event} representing information on the event
 */
public interface Payload {

     enum ContentType {

        JSON ("application/json"),
        PLAIN_TEXT ("text/plain"),
        XML ("text/xml"),
        CSV ("text/csv");

        ContentType (final @NotNull String contentType){
            this.contentType = contentType;
        }

        @JsonProperty("contentType")
        @Schema(description = "The official representation of the content type")
        final @NotNull String contentType;

        public @NotNull String getContentType() {
            return contentType;
        }
    }

    /**
     * @return the content type of the payload
     */
    @JsonProperty("contentType")
    @Schema(description = "The content type of the payload that the event contains",
            required = true)
    @NotNull Payload.ContentType getContentType();

    /**
     * @return the content of the payload
     */
    @JsonProperty("content")
    @Schema(description = "The content of the payload encoded as a string")
    @NotNull String getContent();
}
