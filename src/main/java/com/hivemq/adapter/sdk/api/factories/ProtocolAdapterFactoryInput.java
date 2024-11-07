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
package com.hivemq.adapter.sdk.api.factories;

import com.hivemq.adapter.sdk.api.events.EventService;
import org.jetbrains.annotations.NotNull;

/**
 * This interface offers access to classes that might be needed during instantiation of a ProtocolAdapterFactory
 */
public interface ProtocolAdapterFactoryInput {

    /**
     * @return True: Edge has writing enabled, false: Edge does not support writing.
     */
    boolean isWritingEnabled();

    /**
     * @return {@link EventService} to create and fire events that are displayed at the UI.
     */
    @NotNull
    EventService eventService();

}
