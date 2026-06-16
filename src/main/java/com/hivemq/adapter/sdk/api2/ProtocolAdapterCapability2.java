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
package com.hivemq.adapter.sdk.api2;

/**
 * The optional capabilities a protocol adapter type may declare — exactly the three the framework gates on.
 * Declared once, on {@link ProtocolAdapterInformation2#capabilities()}.
 * <p>
 * There is deliberately no VERIFY capability: verification is always attempted.
 */
public enum ProtocolAdapterCapability2 {
    /**
     * The adapter can subscribe to value changes ({@link ProtocolAdapter2#addSubscriptionBatch(java.util.List)}
     * / {@link ProtocolAdapter2#removeSubscriptionBatch(java.util.List)}).
     */
    SUBSCRIPTIONS,
    /**
     * The adapter can write values southbound ({@link ProtocolAdapter2#writeBatch(java.util.List)}).
     */
    WRITE,
    /**
     * The adapter can enumerate the device's address space
     * ({@link ProtocolAdapter2#browse(com.hivemq.adapter.sdk.api2.model.BrowseFilter)}).
     */
    BROWSE
}
