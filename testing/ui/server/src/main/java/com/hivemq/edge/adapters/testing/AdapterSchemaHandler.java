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
package com.hivemq.edge.adapters.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.adapter.sdk.api.ProtocolAdapterCapability;
import com.hivemq.adapter.sdk.api.ProtocolAdapterCategory;
import com.hivemq.adapter.sdk.api.ProtocolAdapterInformation;
import com.hivemq.adapter.sdk.api.ProtocolAdapterTag;
import com.hivemq.adapter.sdk.api.factories.ProtocolAdapterFactory;
import com.hivemq.edge.adapters.testing.model.ProtocolAdapterType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * HTTP handler that returns protocol adapter types with their schemas.
 * <p>
 * This mimics the HiveMQ Edge API endpoint:
 * GET /api/v1/management/protocol-adapters/types
 * <p>
 * Adapters are discovered via Java ServiceLoader from the classpath.
 */
public class AdapterSchemaHandler implements HttpHandler {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(AdapterSchemaHandler.class);

    private final @NotNull ObjectMapper objectMapper;
    private final @NotNull AdapterSchemaGenerator schemaGenerator;

    public AdapterSchemaHandler() {
        this.objectMapper = new ObjectMapper();
        this.schemaGenerator = new AdapterSchemaGenerator();
    }

    @Override
    public void handle(final @NotNull HttpExchange exchange) throws IOException {
        // Enable CORS for development
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method not allowed");
            return;
        }

        try {
            final List<ProtocolAdapterType> adapterTypes = loadAdapterTypes();

            if (adapterTypes.isEmpty()) {
                LOG.warn("No protocol adapter factories found on classpath.");
                LOG.warn("Make sure your adapter JAR is on the classpath and contains:");
                LOG.warn("  META-INF/services/com.hivemq.adapter.sdk.api.factories.ProtocolAdapterFactory");
            } else {
                LOG.info("Found {} adapter type(s): {}",
                        adapterTypes.size(),
                        adapterTypes.stream().map(ProtocolAdapterType::getId).collect(Collectors.joining(", ")));
            }

            // Wrap in {"items": [...]} format to match Edge API
            final Map<String, Object> response = new LinkedHashMap<>();
            response.put("items", adapterTypes);

            final byte[] body = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(response);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);

            try (final OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }

        } catch (final Exception e) {
            LOG.error("Error generating adapter schemas", e);
            sendError(exchange, 500, "Internal server error: " + e.getMessage());
        }
    }

    private @NotNull List<ProtocolAdapterType> loadAdapterTypes() {
        final List<ProtocolAdapterType> types = new ArrayList<>();

        @SuppressWarnings("rawtypes")
        final ServiceLoader<ProtocolAdapterFactory> loader = ServiceLoader.load(
                ProtocolAdapterFactory.class,
                Thread.currentThread().getContextClassLoader());

        for (final ProtocolAdapterFactory<?> factory : loader) {
            try {
                final ProtocolAdapterType type = toProtocolAdapterType(factory);
                types.add(type);
            } catch (final Exception e) {
                LOG.error("Error loading adapter factory: {}", factory.getClass().getName(), e);
            }
        }

        return types;
    }

    private @NotNull ProtocolAdapterType toProtocolAdapterType(
            final @NotNull ProtocolAdapterFactory<?> factory) {
        final ProtocolAdapterInformation info = factory.getInformation();

        // Generate JSON Schema from config class
        final JsonNode configSchema = schemaGenerator.generateJsonSchema(
                info.configurationClassNorthbound());

        // Parse UI Schema if present
        JsonNode uiSchema = null;
        final String uiSchemaJson = info.getUiSchema();
        if (uiSchemaJson != null && !uiSchemaJson.isBlank()) {
            try {
                uiSchema = objectMapper.readTree(uiSchemaJson);
            } catch (final Exception e) {
                LOG.warn("Failed to parse UI schema for adapter {}: {}",
                        info.getProtocolId(), e.getMessage());
            }
        }

        // Convert capabilities to strings
        final Set<String> capabilities = info.getCapabilities().stream()
                .map(ProtocolAdapterCapability::name)
                .collect(Collectors.toSet());

        // Convert tags to strings
        final List<String> tags = info.getTags() != null ?
                info.getTags().stream().map(ProtocolAdapterTag::name).collect(Collectors.toList()) :
                null;

        // Get category name
        final ProtocolAdapterCategory category = info.getCategory();
        final String categoryName = category != null ? category.name() : null;

        return new ProtocolAdapterType(
                info.getProtocolId(),
                info.getProtocolName(),
                info.getDisplayName(),
                info.getDescription(),
                info.getUrl(),
                info.getVersion(),
                info.getLogoUrl(),
                info.getAuthor(),
                categoryName,
                tags,
                capabilities,
                configSchema,
                uiSchema
        );
    }

    private void sendError(
            final @NotNull HttpExchange exchange,
            final int statusCode,
            final @NotNull String message) throws IOException {
        final Map<String, String> error = Map.of("error", message);
        final byte[] body = objectMapper.writeValueAsBytes(error);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, body.length);

        try (final OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }
}
