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
package com.hivemq.adapter.sdk.api2.node;

import java.util.EnumSet;
import org.jetbrains.annotations.NotNull;

/**
 * The protocol-specific half of the inseparable Node/Tag pair — a passive data object, subclassed per
 * protocol. The protocol adapter sees {@code Node} ONLY: it never sees {@link Tag2}, mappings, or anything on
 * the Edge side. Correlation across the adapter boundary is by {@code Node} reference — there is no lookup map
 * anywhere.
 * <p>
 * JACKSON CONTRACT: a node definition travels as JSON. {@link #nodeString()} is the full Jackson
 * serialization of the subclass's fields, and the framework reads a stored node string back with Jackson
 * against {@link com.hivemq.adapter.sdk.api2.ProtocolAdapterInformation2#nodeClass()}. Only fields are
 * serialized: the computed accessors ({@code nodeId()}, {@code nodeString()}, {@code properties()}) are not
 * bean getters and therefore never appear in the JSON. A node with no fields set is still valid — it
 * serializes an empty JSON object and is a legitimate browse filter
 * ({@link com.hivemq.adapter.sdk.api2.command.BrowseFilter} for "browse from the root").
 */
public abstract class Node {

    /**
     * @return the minimum identity of this node (present on {@link NodeProperty#UNIQUE} nodes).
     */
    public abstract @NotNull String nodeId();

    /**
     * @return the full JSON serialization of this node definition (always present, possibly {@code {}}).
     */
    public abstract @NotNull String nodeString();

    /**
     * The properties are COMPUTED from the subclass's fields on every call and are never serialized.
     * <p>
     * Property implication: where the protocol implies it, {@link NodeProperty#UNIQUE} ⇒
     * {@link NodeProperty#TYPED} — an address that pins a node unambiguously often pins its data type as
     * well (an OPC UA node id resolves to exactly one typed node). Subclasses whose protocol carries that
     * implication must include {@code TYPED} whenever they report {@code UNIQUE}; protocols where identity
     * and type are independent (a raw register address with a separately declared type) report the two
     * properties independently.
     *
     * @return the computed, never-serialized properties of this node.
     */
    public abstract @NotNull EnumSet<NodeProperty> properties();

    /**
     * @param property the property to test for.
     * @return whether this node has the given computed property.
     */
    public final boolean is(final @NotNull NodeProperty property) {
        return properties().contains(property);
    }
}
