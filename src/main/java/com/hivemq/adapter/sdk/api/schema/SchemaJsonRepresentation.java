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
package com.hivemq.adapter.sdk.api.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Jackson-based converter between {@link Schema} objects and JSON Schema documents.
 */
public final class SchemaJsonRepresentation {
    static final @NotNull String SCHEMA_URI = "https://json-schema.org/draft/2019-09/schema";
    public static final @NotNull SchemaJsonRepresentation INSTANCE = new SchemaJsonRepresentation();

    private static final @NotNull ObjectMapper MAPPER = new ObjectMapper();

    public SchemaJsonRepresentation() {}

    // ── Schema → JSON Schema ─────────────────────────────────────────────────

    /**
     * Returns a JSON Schema representation of the given schema as a JSON string.
     */
    public @NotNull String toJsonSchemaString(final @NotNull Schema schema) {
        return toJsonSchema(schema).toString();
    }

    public ObjectNode toCompositeSchema(final @NotNull TagSchemaCreationOutput.DataPointSchema dps) {
        final var builder = new SchemaBuilder().startObject();

        builder.property("tagName").scalar(ScalarType.STRING).readable().writable(false);
        builder.property("timestamp").scalar(ScalarType.LONG).readable().writable(false);
        builder.property("value").required().schema(dps.valueSchema()).readable().writable();



        if (dps.metaData() != null) {
            builder.property("metadata").schema(dps.metaData()).readable().writable(false);
        }
        if (dps.context() != null) {
            builder.property("context").schema(dps.context()).readable().writable(false);
        }

        final var result = toJsonSchema(builder.endObject().build());
        result.set("$schema", new TextNode(SCHEMA_URI));
        return result;
    }

    public @NotNull ObjectNode toJsonSchema(final @NotNull Schema schema) {
        final var node = JsonNodeFactory.instance.objectNode();
        return switch (schema) {
            case final AnySchema a -> {
                applyAnnotations(a, node);
                yield node;
            }
            case final ScalarSchema s -> {
                final var typeStr = toJsonTypeString(s.type());
                if (s.nullable()) {
                    final var arr = node.putArray("type");
                    arr.add(typeStr);
                    arr.add("null");
                } else {
                    node.put("type", typeStr);
                }
                if (s.type() == ScalarType.BINARY) {
                    node.put("contentEncoding", "base64");
                }
                if (s.type() == ScalarType.LONG || s.type() == ScalarType.ULONG) {
                    if (s.minimum() != null) {
                        node.put("minimum", s.minimum().longValue());
                    }
                    if (s.maximum() != null) {
                        node.put("maximum", s.maximum().longValue());
                    }
                } else if (s.type() == ScalarType.DOUBLE) {
                    if (s.minimum() != null) {
                        node.put("minimum", s.minimum().doubleValue());
                    }
                    if (s.maximum() != null) {
                        node.put("maximum", s.maximum().doubleValue());
                    }
                }
                applyAnnotations(s, node);
                yield node;
            }
            case final ObjectSchema o -> {
                if (o.nullable()) {
                    final var anyOf = node.putArray("anyOf");
                    anyOf.add(buildObjectNode(o));
                    anyOf.addObject().put("type", "null");
                    applyAnnotations(o, node);
                    yield node;
                }
                final var on = buildObjectNode(o);
                applyAnnotations(o, on);
                yield on;
            }
            case final ArraySchema a -> {
                if (a.nullable()) {
                    final var anyOf = node.putArray("anyOf");
                    anyOf.add(buildArrayNode(a));
                    anyOf.addObject().put("type", "null");
                    applyAnnotations(a, node);
                    yield node;
                }
                final var an = buildArrayNode(a);
                applyAnnotations(a, an);
                yield an;
            }
        };
    }

    private @NotNull ObjectNode buildObjectNode(final @NotNull ObjectSchema o) {
        final var node = JsonNodeFactory.instance.objectNode();
        node.put("type", "object");
        final var props = node.putObject("properties");
        o.properties().forEach((key, s) -> props.set(key, toJsonSchema(s)));
        if (!o.required().isEmpty()) {
            final var arr = node.putArray("required");
            o.required().forEach(arr::add);
        }
        if (!o.additionalProperties()) {
            node.put("additionalProperties", false);
        }
        return node;
    }

    private @NotNull ObjectNode buildArrayNode(final @NotNull ArraySchema a) {
        final var node = JsonNodeFactory.instance.objectNode();
        node.put("type", "array");
        node.set("items", toJsonSchema(a.items()));
        if (a.minContains() != null) {
            node.put("minContains", a.minContains());
        }
        if (a.maxContains() != null) {
            node.put("maxContains", a.maxContains());
        }
        return node;
    }

    private static @NotNull String toJsonTypeString(final @NotNull ScalarType type) {
        return switch (type) {
            case BOOLEAN -> "boolean";
            case LONG, ULONG -> "integer";
            case DOUBLE -> "number";
            case STRING, BINARY -> "string";
        };
    }

    private static void applyAnnotations(final @NotNull Schema schema, final @NotNull ObjectNode node) {
        if (schema.title() != null) {
            node.put("title", schema.title());
        }
        if (schema.description() != null) {
            node.put("description", schema.description());
        }
        if (!schema.writable()) {
            node.put("readOnly", true);
        }
        if (!schema.readable()) {
            node.put("writeOnly", true);
        }
    }

    // ── JSON Schema → Schema ─────────────────────────────────────────────────

    /**
     * Parses a JSON Schema string and reconstructs the corresponding {@link Schema}.
     */
    public @NotNull Schema fromJsonSchemaString(final @NotNull String json) {
        try {
            return fromJsonSchema((ObjectNode) MAPPER.readTree(json));
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON impl: " + e.getMessage(), e);
        }
    }

    public @NotNull Schema fromJsonSchema(final @NotNull ObjectNode node) {
        final String title = node.has("title") ? node.get("title").asText() : null;
        final String description =
                node.has("description") ? node.get("description").asText() : null;
        final boolean writable = !(node.has("readOnly") && node.get("readOnly").asBoolean());
        final boolean readable =
                !(node.has("writeOnly") && node.get("writeOnly").asBoolean());

        final var typeNode = node.get("type");

        if (node.has("properties") || isType(typeNode, "object")) {
            final var props = new LinkedHashMap<String, Schema>();
            if (node.has("properties")) {
                node.get("properties")
                        .fields()
                        .forEachRemaining(e -> props.put(e.getKey(), fromJsonSchema((ObjectNode) e.getValue())));
            }
            final var req = new ArrayList<String>();
            if (node.has("required")) {
                node.get("required").forEach(n -> req.add(n.asText()));
            }
            final List<String> required = List.copyOf(req);
            final boolean addlProps = !node.has("additionalProperties")
                    || node.get("additionalProperties").asBoolean();
            return new ObjectSchema(
                    Map.copyOf(props), required, addlProps, title, description, false, readable, writable);
        }

        if (node.has("items") || isType(typeNode, "array")) {
            final Schema items = node.has("items")
                    ? fromJsonSchema((ObjectNode) node.get("items"))
                    : new AnySchema(null, null, false, true, false);
            final Integer minContains =
                    node.has("minContains") ? node.get("minContains").asInt() : null;
            final Integer maxContains =
                    node.has("maxContains") ? node.get("maxContains").asInt() : null;
            return new ArraySchema(items, minContains, maxContains, title, description, false, readable, writable);
        }

        if (typeNode != null) {
            boolean nullable = false;
            ScalarType primaryType = null;
            if (typeNode.isArray()) {
                for (final var n : typeNode) {
                    if ("null".equals(n.asText())) {
                        nullable = true;
                    } else {
                        primaryType = fromJsonTypeString(n.asText());
                    }
                }
            } else {
                primaryType = fromJsonTypeString(typeNode.asText());
            }
            if (primaryType == null) {
                throw new IllegalArgumentException("JSON Schema type array contains no non-null type");
            }
            final Number minimum = node.has("minimum") ? node.get("minimum").numberValue() : null;
            final Number maximum = node.has("maximum") ? node.get("maximum").numberValue() : null;
            return new ScalarSchema(
                    primaryType, minimum, maximum, title, description, nullable, readable, writable);
        }

        return new AnySchema(title, description, false, readable, writable);
    }

    private static boolean isType(final JsonNode typeNode, final String typeName) {
        if (typeNode == null) {
            return false;
        }
        if (typeNode.isTextual()) {
            return typeName.equals(typeNode.asText());
        }
        for (final var n : typeNode) {
            if (typeName.equals(n.asText())) {
                return true;
            }
        }
        return false;
    }

    private static @NotNull ScalarType fromJsonTypeString(final @NotNull String type) {
        return switch (type) {
            case "boolean" -> ScalarType.BOOLEAN;
            case "integer" -> ScalarType.LONG;
            case "number" -> ScalarType.DOUBLE;
            case "string" -> ScalarType.STRING;
            default -> throw new IllegalArgumentException("Unknown JSON Schema type: " + type);
        };
    }
}
