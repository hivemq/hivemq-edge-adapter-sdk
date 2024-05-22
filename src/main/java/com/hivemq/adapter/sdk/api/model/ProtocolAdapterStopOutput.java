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
package com.hivemq.adapter.sdk.api.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ProtocolAdapterStopOutput {

    /**
     * Signals HiveMQ Edge that this stop attempt was successful.
     * This method may be called asynchronously after the stop() method of the adapter has ended.
     */
    void stoppedSuccessfully();

    /**
     * Signals HiveMQ Edge that this stop attempt failed.
     * This method may be called asynchronously after the stop() method of the adapter has ended.
     *
     * @param throwable    a throwable signaling the reason why the start failed.
     * @param errorMessage Optional error message to be logged
     */
    void failStop(@NotNull Throwable throwable, @Nullable String errorMessage);

}
