/*
 * Copyright 2024-present HiveMQ GmbH
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
package com.hivemq.adapter.sdk.api.data;

import com.hivemq.adapter.sdk.api.factories.AdapterFactories;
import com.hivemq.adapter.sdk.api.factories.DataPointFactory;
import com.hivemq.adapter.sdk.api.model.ProtocolAdapterInput;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for data points gathered by a protocol adapter.
 * Instances of it can be created via {@link DataPointFactory} accessible via {@link AdapterFactories} in the {@link ProtocolAdapterInput}.
 */
public interface DataPoint {
    /**
     * @return the tag value of the data point.
     */
    @NotNull Object getTagValue();

    /**
     * @return the tag name of the data point.
     */
    @NotNull String getTagName();
}
