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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * HTTP handler that serves static files from a directory.
 * <p>
 * This is used to serve the pre-built React application.
 * Supports SPA (Single Page Application) routing by returning index.html
 * for paths that don't map to existing files.
 */
public class StaticFileHandler implements HttpHandler {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(StaticFileHandler.class);

    private static final @NotNull Map<String, String> MIME_TYPES = Map.ofEntries(
            Map.entry(".html", "text/html"),
            Map.entry(".htm", "text/html"),
            Map.entry(".css", "text/css"),
            Map.entry(".js", "application/javascript"),
            Map.entry(".mjs", "application/javascript"),
            Map.entry(".json", "application/json"),
            Map.entry(".png", "image/png"),
            Map.entry(".jpg", "image/jpeg"),
            Map.entry(".jpeg", "image/jpeg"),
            Map.entry(".gif", "image/gif"),
            Map.entry(".svg", "image/svg+xml"),
            Map.entry(".ico", "image/x-icon"),
            Map.entry(".woff", "font/woff"),
            Map.entry(".woff2", "font/woff2"),
            Map.entry(".ttf", "font/ttf"),
            Map.entry(".eot", "application/vnd.ms-fontobject"),
            Map.entry(".map", "application/json"),
            Map.entry(".txt", "text/plain")
    );

    private final @NotNull Path rootDir;

    public StaticFileHandler(final @NotNull Path rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public void handle(final @NotNull HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // Default to index.html for root
        if (path.equals("/")) {
            path = "/index.html";
        }

        // Security: prevent directory traversal
        final Path requestedPath = rootDir.resolve(path.substring(1)).normalize();
        if (!requestedPath.startsWith(rootDir)) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        if (Files.exists(requestedPath) && Files.isRegularFile(requestedPath)) {
            serveFile(exchange, requestedPath);
        } else {
            // SPA fallback: serve index.html for client-side routing
            final Path indexPath = rootDir.resolve("index.html");
            if (Files.exists(indexPath)) {
                serveFile(exchange, indexPath);
            } else {
                sendError(exchange, 404, "Not found: " + path);
            }
        }
    }

    private void serveFile(
            final @NotNull HttpExchange exchange,
            final @NotNull Path filePath) throws IOException {
        final byte[] content = Files.readAllBytes(filePath);
        final String contentType = getContentType(filePath);

        exchange.getResponseHeaders().set("Content-Type", contentType);

        // Cache static assets (CSS, JS, images)
        if (isStaticAsset(filePath)) {
            exchange.getResponseHeaders().set("Cache-Control", "public, max-age=31536000");
        } else {
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        }

        exchange.sendResponseHeaders(200, content.length);

        try (final OutputStream os = exchange.getResponseBody()) {
            os.write(content);
        }
    }

    private @NotNull String getContentType(final @NotNull Path filePath) {
        final String fileName = filePath.getFileName().toString().toLowerCase();
        final int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            final String extension = fileName.substring(dotIndex);
            return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
        }
        return "application/octet-stream";
    }

    private boolean isStaticAsset(final @NotNull Path filePath) {
        final String fileName = filePath.getFileName().toString().toLowerCase();
        // Assets typically have hashes in their names (e.g., index-abc123.js)
        // or are in an assets directory
        return filePath.toString().contains("/assets/") ||
                fileName.matches(".*-[a-f0-9]+\\.(js|css|png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot)$");
    }

    private void sendError(
            final @NotNull HttpExchange exchange,
            final int statusCode,
            final @NotNull String message) throws IOException {
        final byte[] body = message.getBytes();

        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(statusCode, body.length);

        try (final OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }
}
