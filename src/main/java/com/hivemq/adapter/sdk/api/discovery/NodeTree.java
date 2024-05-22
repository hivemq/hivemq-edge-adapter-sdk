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
package com.hivemq.adapter.sdk.api.discovery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface is used to create a tree of nodes representing the available tags discovered by this adapter.
 */
public interface NodeTree {

    /**
     * Add a new node to this tree
     * @param id the unique id of the new node.
     * @param name the name of the new node.
     * @param description a description for this node.
     * @param parentId the id of the parent node.
     * @param nodeType the type of the node.
     * @param selectable Whether this node can be selected as a data source.
     */
    void addNode(
            @NotNull String id,
            @NotNull String name,
            @NotNull String description,
            @Nullable String parentId,
            @NotNull NodeType nodeType,
            boolean selectable);

}


