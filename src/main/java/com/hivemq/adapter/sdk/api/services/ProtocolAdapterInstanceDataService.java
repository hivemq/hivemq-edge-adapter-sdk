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
package com.hivemq.adapter.sdk.api.services;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service class to allow adapters to persist temporary data (e.g. sessionids, subscription ids ...).
 */
public interface ProtocolAdapterInstanceDataService {

    /**
     * Store a temporary value for the given key.
     * Exisiting values  will be overwritten.
     *
     * @param key   the key to store the value under
     * @param value the value to store, null indicating a deletion
     */
    @NotNull CompletableFuture<Void> putValue(@NotNull String key, @Nullable String value);

    /**
     * Retrieve a temporary value for the given key.
     *
     * @param key the key to retrieve the value for
     * @return the stored value or an empty optional if no value is stored for the given key
     */
    @NotNull CompletableFuture<Optional<String>> getValue(@NotNull String key);

}
