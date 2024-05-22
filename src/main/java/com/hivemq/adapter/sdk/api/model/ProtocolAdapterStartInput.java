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
package com.hivemq.adapter.sdk.api.model;

import com.hivemq.adapter.sdk.api.services.ModuleServices;
import org.jetbrains.annotations.NotNull;

/**
 * Object to access information and Services necessary for the start of adapters.
 */
public interface ProtocolAdapterStartInput {

    /**
     * @return Object that contains a variety of services useful for the start of a protocol adapter.
     */
    @NotNull ModuleServices moduleServices();

}
