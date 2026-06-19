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

import com.hivemq.adapter.sdk.api.discovery.NodeType;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import org.jetbrains.annotations.NotNull;

/**
 * One entry of a browse result. The node kind is the reused v1 {@link NodeType}.
 * <p>
 * {@code browseName} is the node's human-meaningful name within its parent (for OPC-UA, the BrowseName
 * attribute, e.g. {@code "Temperature"}) — distinct from {@link Node#nodeId()}, which is the machine address.
 * The framework needs it <b>at discovery time</b> to assemble a node's path from its ancestors' browse names
 * (e.g. {@code /Plant/Line1/Temperature}) and from that a default tag name; this cannot be recovered later,
 * because the parentage is known only while walking. Empty when the protocol has no such name.
 *
 * @param node       the discovered node.
 * @param type       the kind of node (folder, object, or value).
 * @param selectable whether the node can be selected as a tag's node definition.
 * @param browseName the node's name within its parent, used to build paths and default tag names.
 */
public record BrowseResultEntry(
        @NotNull Node node, @NotNull NodeType type, boolean selectable, @NotNull String browseName) {
}
