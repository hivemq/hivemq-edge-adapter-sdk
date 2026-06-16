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
/**
 * Adapter factories: {@link com.hivemq.adapter.sdk.api2.factories.ProtocolAdapterFactory2} constructs adapter
 * instances from a {@link com.hivemq.adapter.sdk.api2.model.ProtocolAdapterInput2} (in the {@code model}
 * package), which carries the {@link com.hivemq.adapter.sdk.api2.services.ProtocolAdapterService} the framework
 * provides.
 * <p>
 * Capabilities live solely on {@link com.hivemq.adapter.sdk.api2.ProtocolAdapterInformation2}; instance
 * construction is synchronous and cheap (no I/O, no connection).
 */
package com.hivemq.adapter.sdk.api2.factories;
