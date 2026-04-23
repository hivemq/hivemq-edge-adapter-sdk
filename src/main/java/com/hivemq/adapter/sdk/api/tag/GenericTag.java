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
package com.hivemq.adapter.sdk.api.tag;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The canonical Tag implementation owned by Edge. Protocol adapters that have migrated away from their
 * own XxxTag class use this class instead.
 * <p>
 * The {@code scope} field holds the adapter instance ID and is set by Edge infrastructure after construction.
 * It is excluded from {@code equals} and {@code hashCode} as it is infrastructure metadata, not tag identity.
 */
public class GenericTag implements Tag {

    private final @NotNull String name;
    private final @NotNull String description;
    private final @NotNull TagDefinition definition;
    private @NotNull String scope = "";

    public GenericTag(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull TagDefinition definition) {
        this.name = name;
        this.description = description;
        this.definition = definition;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @NotNull TagDefinition getDefinition() {
        return definition;
    }

    @Override
    public @NotNull String getScope() {
        return scope;
    }

    @Override
    public void setScope(final @NotNull String scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GenericTag that)) return false;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(definition, that.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, definition);
    }

    @Override
    public String toString() {
        return "GenericTag{name='" + name + "', description='" + description
                + "', definition=" + definition + "', scope='" + scope + "'}";
    }
}
