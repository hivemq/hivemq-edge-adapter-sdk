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
package com.hivemq.adapter.sdk.api.discovery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface ProtocolAdapterDiscoveryOutput {

    /**
     * @return the tree to which the discovered nodes should be added.
     */
    @NotNull NodeTree getNodeTree();

    /**
     * Signals Edge that all data is discovered.
     */
    void finish();

    /**
     * Signals that something went wrong during discovery.
     *
     * @param t Throwable indicating what went wrong.
     * @param errorMessage an optional error message delivering further insights.
     */
    void fail(@NotNull Throwable t, @Nullable String errorMessage);

    /**
     * Signals that something went wrong during discovery.
     *
     * @param errorMessage a message indicating what went wrong.
     */
    void fail(@NotNull String errorMessage);

}
