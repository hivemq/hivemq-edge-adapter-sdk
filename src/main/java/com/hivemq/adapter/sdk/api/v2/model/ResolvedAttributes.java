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

import com.hivemq.adapter.sdk.api.v2.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.v2.node.AccessFlags;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import org.jetbrains.annotations.NotNull;

/**
 * The device-resolved attributes of one discovered node — the RESOLVE half of a browse. After
 * {@link ProtocolAdapter#browse(int, BrowseFilter, int)} DISCOVERs the variables, the framework asks the
 * adapter to RESOLVE their attributes with {@link ProtocolAdapter#readNodeAttributes(int, java.util.List)}; one
 * of these is reported per resolved node in the answering
 * {@link ProtocolAdapterOutput#readAttributesResult(int, java.util.List)}.
 * <p>
 * {@code dataType} is the protocol's own datatype identifier as a string — the SDK does not impose a
 * cross-protocol datatype vocabulary; the schema/import layer maps it (for OPC-UA it is the DataType node's
 * parseable id, e.g. {@code "i=6"} for Int32). {@code access} reuses the SDK's {@link AccessFlags}.
 *
 * @param node        the resolved node (the correlation key, as everywhere on the output façade).
 * @param dataType    the protocol datatype identifier of the node's value.
 * @param access      the node's declared access capabilities.
 * @param description a human-readable description of the node, or empty if the device declares none.
 */
public record ResolvedAttributes(
        @NotNull Node node,
        @NotNull String dataType,
        @NotNull AccessFlags access,
        @NotNull String description) {
}
