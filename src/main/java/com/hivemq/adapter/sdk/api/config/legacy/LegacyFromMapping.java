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
package com.hivemq.adapter.sdk.api.config.legacy;

import com.hivemq.adapter.sdk.api.config.MessageHandlingOptions;
import org.jetbrains.annotations.NotNull;

public class LegacyFromMapping {

    private final @NotNull String topic;
    private final @NotNull String tagName;
    private final int maxQoS;
    private final @NotNull MessageHandlingOptions messageHandlingOptions;

    LegacyFromMapping(
            final @NotNull String topic,
            final @NotNull String tagName,
            final int maxQoS,
            final @NotNull MessageHandlingOptions messageHandlingOptions) {
        this.topic = topic;
        this.tagName = tagName;
        this.maxQoS = maxQoS;
        this.messageHandlingOptions = messageHandlingOptions;
    }

    public int getMaxQoS() {
        return maxQoS;
    }

    public @NotNull String getTagName() {
        return tagName;
    }

    public @NotNull String getTopic() {
        return topic;
    }
}
