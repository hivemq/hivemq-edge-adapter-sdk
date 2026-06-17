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
package com.hivemq.adapter.sdk.api.v2.node;

/**
 * Computed, never-serialized properties of a {@link Node}.
 */
public enum NodeProperty {
    /**
     * The node carries enough identity to be addressed unambiguously ({@link Node#nodeId()} is meaningful).
     */
    UNIQUE,
    /**
     * The node's data type is known.
     */
    TYPED,
    /**
     * The node definition is internally consistent and complete enough to be used.
     */
    VALID
}
