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
package com.hivemq.adapter.sdk.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ProtocolAdapterCategory {
    CONNECTIVITY("Connectivity", "A standard connectivity based protocol, typically web standard.", null),
    INDUSTRIAL("Industrial", "Industrial, typically field bus protocols.", null),
    BUILDING_AUTOMATION("Building Automation", "Protocols related to building automation", null),
    SIMULATION("Simulation", "Simulation protocols, that emulate real world devices", null);

    ProtocolAdapterCategory(
            final @NotNull String displayName,
            final @NotNull String description,
            final @Nullable String image) {
        this.displayName = displayName;
        this.image = image;
        this.description = description;
    }

    final @NotNull String displayName;
    final @NotNull String description;
    final @Nullable String image;

    public @NotNull String getDisplayName() {
        return displayName;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public @Nullable String getImage() {
        return image;
    }
}
