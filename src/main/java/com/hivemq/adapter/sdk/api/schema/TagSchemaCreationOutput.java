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

    void finish(@NotNull DataPointSchema schema);

    /**
     * Finishes the creation of the schema successfully.
     * @deprecated use {@link #finish(DataPointSchema)} instead to provide more information about the created impl.
     */
    @Deprecated
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
     * Signals that something went wrong during the creation of the json impl.
     *
     * @param t Throwable indicating what went wrong.
     * @param errorMessage an optional error message delivering further insights.
     */
    void fail(@NotNull Throwable t, @Nullable String errorMessage);

    /**
     * Signals that something went wrong during the creation of the json impl.
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

    /**
     * The schema(s) describing a tag's data point.
     *
     * @param valueSchema       the readable value shape (northbound / read direction).
     * @param metadataSchema    optional metadata shape; read-only.
     * @param context           optional context shape; read-only.
     * @param southboundSchema  optional explicit southbound (write) shape. When {@code null}, the southbound view
     *                          falls back to {@code valueSchema}. Set this only when the southbound shape is
     *                          <em>not</em> a projection of the read shape — e.g. an OPC-UA condition tag whose
     *                          write target is {@code {eventId, method, comment}}.
     *                          <p>
     *                          <b>Mark it writable.</b> {@link SchemaBuilder} defaults every node to
     *                          {@code writable = false}, which renders as {@code readOnly: true}. A consumer of
     *                          the southbound schema — including Edge's own mapping editor — treats a read-only
     *                          field as a non-destination and does not offer it, so a southbound schema built
     *                          without {@code .writable()} on its root and on each writable member describes a
     *                          shape with no write destinations at all. Chain {@code .writable()} explicitly:
     *                          <pre>{@code
     * new SchemaBuilder()
     *         .startObject()
     *         .property("eventId").required().scalar(ScalarType.STRING).writable()
     *         .property("method").required().scalar(ScalarType.LONG).writable()
     *         .property("comment").scalar(ScalarType.STRING).writable()
     *         .endObject()
     *         .writable()
     *         .build()
     * }</pre>
     *                          <p>
     *                          Read-only fields are <em>not</em> pruned from the southbound view: write-permission
     *                          cannot be expressed correctly by a static schema (an array of read-only items admits
     *                          only {@code []}; a required read-only member makes the object unsatisfiable). Note
     *                          that {@code readOnly} is <em>descriptive metadata only</em> — it is a JSON Schema
     *                          annotation, not an assertion, and no Edge runtime currently rejects a write because
     *                          it carries a read-only field. Do not rely on it as a safety boundary.
     */
    record DataPointSchema(
            @NotNull Schema valueSchema,
            @Nullable Schema metadataSchema,
            @Nullable Schema context,
            @Nullable Schema southboundSchema) {

        /**
         * Convenience constructor for the common case: no explicit southbound schema (the southbound view is
         * derived from {@code valueSchema}).
         */
        public DataPointSchema(
                final @NotNull Schema valueSchema,
                final @Nullable Schema metadataSchema,
                final @Nullable Schema context) {
            this(valueSchema, metadataSchema, context, null);
        }

        /**
         * @deprecated "write" is only accurate for a variable-shaped tag. For a condition, method or polled tag
         *     the southbound shape is a request/command, not a written projection of state — which is precisely
         *     the case this component exists for. Use {@link #southboundSchema()}, matching the
         *     {@code NORTHBOUND} / {@code SOUTHBOUND} vocabulary of the public REST API.
         */
        @Deprecated(forRemoval = true)
        public @Nullable Schema writeSchema() {
            return southboundSchema();
        }
    }
}
