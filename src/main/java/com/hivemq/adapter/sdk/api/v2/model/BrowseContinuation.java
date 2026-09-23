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
package com.hivemq.adapter.sdk.api.v2.model;

import org.jetbrains.annotations.NotNull;

/**
 * An opaque continuation token for a paginated browse. The adapter produces one on a
 * {@link ProtocolAdapterOutput#browsePage(int, java.util.List, BrowseContinuation)} when more pages remain,
 * and the framework hands it back verbatim on
 * {@link com.hivemq.adapter.sdk.api.v2.ProtocolAdapter#browseNext(int, BrowseContinuation)}. It is opaque to
 * the framework — the adapter owns the encoding (for example an OPC-UA continuation point, base64-encoded).
 *
 * @param token the adapter-owned opaque resume token.
 */
public record BrowseContinuation(@NotNull String token) {
}
