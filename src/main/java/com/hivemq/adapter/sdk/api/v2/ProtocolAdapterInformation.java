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
package com.hivemq.adapter.sdk.api.v2;

import com.hivemq.adapter.sdk.api.ProtocolAdapterCategory;
import com.hivemq.adapter.sdk.api.ProtocolAdapterTag;
import com.hivemq.adapter.sdk.api.v2.node.Node;
import java.util.EnumSet;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Identity, display metadata, and capabilities of a protocol adapter type. This is the <b>single</b> home of
 * {@link #capabilities()} — the factory deliberately has no capability accessor.
 * <p>
 * Category and search tags are the reused v1 enums (the reuse boundary).
 */
public interface ProtocolAdapterInformation {

    /**
     * @return the unique identifier of the protocol this adapter type implements.
     */
    @NotNull String protocolId();

    /**
     * @return the human-readable display name of the adapter type.
     */
    @NotNull String displayName();

    /**
     * @return the human-readable description of the adapter type.
     */
    @NotNull String description();

    /**
     * @return the version of the adapter type.
     */
    @NotNull String version();

    /**
     * @return the URL of the adapter type's logo.
     */
    @NotNull String logoUrl();

    /**
     * @return the author of the adapter type.
     */
    @NotNull String author();

    /**
     * @return the reused v1 category of the adapter type.
     */
    @NotNull ProtocolAdapterCategory category();

    /**
     * @return the reused v1 search tags of the adapter type.
     */
    @NotNull List<ProtocolAdapterTag> tags();

    /**
     * @return the optional capabilities this adapter type declares; the framework gates subscription, write,
     *         and browse behavior on this set.
     */
    @NotNull EnumSet<ProtocolAdapterCapability> capabilities();

    /**
     * @return the concrete {@link Node} subclass of this adapter type, for JSON (de)serialization of node
     *         strings.
     */
    @NotNull Class<? extends Node> nodeClass();

    /**
     * @return the configuration version this adapter type currently writes; v2 framework types report a value
     *         greater than or equal to {@code 2}. A documented secondary signal — the authoritative
     *         discriminator between the legacy and v2 subsystems stays the configuration section and the
     *         factory registry.
     */
    int currentConfigVersion();
}
