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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hivemq.adapter.sdk.api.config.PollingContext;
import com.hivemq.adapter.sdk.api.config.UserProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PollingContextFactory {

    /**
     * @param destination    the mqtt topic on which the data should be published
     * @param qos            the mqtt qos for the data
     * @param userProperties mqtt user properties for the publish that will contain the data
     * @return an {@link PollingContext} containing information how the data will be published.
     */
    @NotNull PollingContext create(
            @JsonProperty("destination") @Nullable final String destination,
            @JsonProperty("qos") final int qos,
            @JsonProperty("userProperties") @Nullable List<UserProperty> userProperties);
}
