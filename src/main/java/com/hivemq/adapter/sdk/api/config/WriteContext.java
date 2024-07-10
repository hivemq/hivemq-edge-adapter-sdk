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
package com.hivemq.adapter.sdk.api.config;

import org.jetbrains.annotations.Nullable;

public interface WriteContext {

    /**
     * @return the mqtt topic from which the data will be read
     */
    @Nullable
    String getSourceMqttTopic();


    /**
     * @return the Quality-of-Service for the MQTT publish containing the source data that will be written
     */
    int getQos();


    /**
     * The minimum time interval between writes. If this is 0 no interval between writes is guaranteed.
     * This interval is intended to protect constrained devices from an overload because of writes.
     *
     * @return long the interval between writes in ms.
     */
    long getWritingInterval();

}