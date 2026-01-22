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

import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

/**
 * A lightweight HTTP server for testing protocol adapter configuration forms.
 * <p>
 * This server provides:
 * <ul>
 *   <li>An API endpoint that returns adapter schemas (mimicking the HiveMQ Edge API)</li>
 *   <li>A static file server for the pre-built React application</li>
 * </ul>
 * <p>
 * Adapters are discovered via ServiceLoader from the classpath.
 * <p>
 * Usage:
 * <pre>
 * java -cp "adapter.jar:testing-ui.jar" com.hivemq.edge.adapters.testing.AdapterTestServer
 * </pre>
 */
public class AdapterTestServer {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(AdapterTestServer.class);

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_FRONTEND_PATH = "frontend/dist";

    public static void main(final @NotNull String[] args) {
        try {
            final int port = getPort();
            final Path frontendPath = getFrontendPath();

            LOG.info("Starting Adapter Test Server...");
            LOG.info("Port: {}", port);
            LOG.info("Frontend path: {}", frontendPath.toAbsolutePath());

            final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // API endpoint - mimics HiveMQ Edge API
            server.createContext("/api/v1/management/protocol-adapters/types",
                    new AdapterSchemaHandler());

            // Static file server for React app
            server.createContext("/", new StaticFileHandler(frontendPath));

            server.setExecutor(Executors.newFixedThreadPool(4));
            server.start();

            final String url = "http://localhost:" + port;
            LOG.info("=".repeat(60));
            LOG.info("Adapter Test Server running at: {}", url);
            LOG.info("=".repeat(60));

            // Try to open browser automatically
            openBrowser(url);

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOG.info("Shutting down server...");
                server.stop(0);
            }));

        } catch (final IOException e) {
            LOG.error("Failed to start server", e);
            System.exit(1);
        }
    }

    private static int getPort() {
        final String portStr = System.getProperty("server.port", System.getenv("PORT"));
        if (portStr != null) {
            try {
                return Integer.parseInt(portStr);
            } catch (final NumberFormatException e) {
                LOG.warn("Invalid port '{}', using default {}", portStr, DEFAULT_PORT);
            }
        }
        return DEFAULT_PORT;
    }

    private static @NotNull Path getFrontendPath() {
        final String pathStr = System.getProperty("frontend.path", System.getenv("FRONTEND_PATH"));
        if (pathStr != null) {
            final Path path = Paths.get(pathStr);
            if (Files.isDirectory(path)) {
                return path;
            }
            LOG.warn("Frontend path '{}' not found, trying default locations", pathStr);
        }

        // Try common locations
        final String[] locations = {
                DEFAULT_FRONTEND_PATH,
                "testing/ui/frontend/dist",
                "../frontend/dist",
                "src/main/resources/static"
        };

        for (final String location : locations) {
            final Path path = Paths.get(location);
            if (Files.isDirectory(path)) {
                return path;
            }
        }

        // Try classpath resource
        try {
            final var resource = AdapterTestServer.class.getClassLoader().getResource("static");
            if (resource != null) {
                return Paths.get(resource.toURI());
            }
        } catch (final Exception e) {
            LOG.debug("Could not load static resources from classpath", e);
        }

        LOG.warn("No frontend directory found. Static files will not be served.");
        return Paths.get(DEFAULT_FRONTEND_PATH);
    }

    private static void openBrowser(final @NotNull String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                LOG.info("Opened browser at {}", url);
            } catch (final Exception e) {
                LOG.debug("Could not open browser automatically", e);
            }
        }
    }
}
