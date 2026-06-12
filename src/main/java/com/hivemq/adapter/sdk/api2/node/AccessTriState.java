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
package com.hivemq.adapter.sdk.api2.node;

/**
 * Three-valued access capability of a node, as declared in {@link AccessFlags}.
 */
public enum AccessTriState {
    /**
     * The capability is available.
     */
    YES,
    /**
     * The capability is not available.
     */
    NO,
    /**
     * The capability may be available on the device, but the configuration declares it unused.
     */
    WILL_NOT_USE
}
