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
package com.hivemq.adapter.sdk.api2.factory;

import com.hivemq.adapter.sdk.api.factories.DataPointFactory;
import com.hivemq.adapter.sdk.api2.actor.Dispatcher;
import org.jetbrains.annotations.NotNull;

/**
 * The services the framework provides to an adapter instance.
 */
public interface ProtocolAdapterServices2 {

    /**
     * @return the reused v1 factory adapter code builds its values with.
     */
    @NotNull DataPointFactory dataPointFactory();

    /**
     * @return the dispatcher an actor-based adapter attaches its mailbox to (single-threaded behavior). An
     *         author who needs a different threading model supplies a different {@link Dispatcher}.
     */
    @NotNull Dispatcher dispatcher();
}
