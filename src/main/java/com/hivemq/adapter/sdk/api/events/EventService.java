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
package com.hivemq.adapter.sdk.api.events;

import com.hivemq.adapter.sdk.api.events.model.Event;
import com.hivemq.adapter.sdk.api.events.model.EventBuilder;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;

import java.util.List;

public interface EventService {

    /**
     * @param adapterId  the id of the adapter for which the event is created
     * @param protocolId the protocol of the adapter for which the event is created
     * @return a {@link EventBuilder} to add further information and build an {@link Event}
     */
    @NotNull
    EventBuilder createAdapterEvent(final @NotNull String adapterId, final @NotNull String protocolId);


    @NotNull
    EventBuilder bridgeEvent();

    /**
     * Reads the currently available events.
     * <p>
     * The amount of events stored is limited and therefore this list may not contain the full history of all events.
     *
     * @param sinceTimestamp events before this timestamp will not be returned.
     * @param limit          how many events should be fetched
     * @return a sorted list of events from latest events to older events.
     */
    @NotNull
    List<Event> readEvents(final @Nullable Long sinceTimestamp, final @Nullable Integer limit);


}
