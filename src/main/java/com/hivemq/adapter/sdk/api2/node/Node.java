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
 */
public abstract class Node {

    /**
     * @return the minimum identity of this node (present on {@link NodeProperty#UNIQUE} nodes).
     */
    public abstract @NotNull String nodeId();

    /**
     * @return the full JSON serialization of this node definition (always present).
     */
    public abstract @NotNull String nodeString();

    /**
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
