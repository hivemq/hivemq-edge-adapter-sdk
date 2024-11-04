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
package com.hivemq.adapter.sdk.api.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TagSchemaCreationOutput {

    /**
     * Finishes the creation of the schema successfully.
     */
    void finish(@NotNull JsonNode schema);

    /**
     * Signals Edge that this adapter can not produce tag schemas.
     */
    void notSupported();

    /**
     * Signals Edge that this adapter is not yet started and therefor can not create a json schema for the given tag.
     */
    void adapterNotStarted();

    /**
     * Signals that something went wrong during the creation of the json schema.
     *
     * @param t Throwable indicating what went wrong.
     * @param errorMessage an optional error message delivering further insights.
     */
    void fail(@NotNull Throwable t, @Nullable String errorMessage);

    /**
     * Signals that something went wrong during the creation of the json schema.
     *
     * @param errorMessage an error message delivering further insights.
     */
    void fail(@NotNull String errorMessage);

    /**
     * Signals that the tag was not found on the PLC and as a result no json schema can be created.
     *
     * @param errorMessage an error message delivering further insights.
     */
    void tagNotFound(@NotNull String errorMessage);
}
