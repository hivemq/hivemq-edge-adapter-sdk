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
package com.hivemq.adapter.sdk.api2.command;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import com.hivemq.adapter.sdk.api2.node.Node;
import org.jetbrains.annotations.NotNull;

/**
 * One entry of a write batch: the target node and the southbound value to write. The value is the reused v1
 * {@link DataPoint}.
 *
 * @param node  the node to write to.
 * @param value the value to write.
 */
public record WriteEntry(@NotNull Node node, @NotNull DataPoint value) {
}
