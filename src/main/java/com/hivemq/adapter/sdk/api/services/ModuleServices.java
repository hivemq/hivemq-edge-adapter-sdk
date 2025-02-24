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

import com.hivemq.adapter.sdk.api.events.EventService;
import com.hivemq.adapter.sdk.api.streaming.ProtocolAdapterTagStreamingService;
import org.jetbrains.annotations.NotNull;

/**
 * This class offers access to various services for adapters.
 */
public interface ModuleServices {

    /**
     * @return the {@link ProtocolAdapterPublishService} that enables adapters to send publishes directly to HiveMQ Edge.
     */
    @NotNull ProtocolAdapterPublishService adapterPublishService();

    /**
     * @return the {@link ProtocolAdapterTagStreamingService} that enables adapters to share new values received for a tag to HiveMQ Edge.
     */
    @NotNull ProtocolAdapterTagStreamingService protocolAdapterTagStreamingService();

    /**
     * @return the {@link EventService} to send events from this adapter to HiveMQ Edge in case something happens the user should be alerted of.
     */
    @NotNull EventService eventService();

    @NotNull ProtocolAdapterWritingService protocolAdapterWritingService();

}
