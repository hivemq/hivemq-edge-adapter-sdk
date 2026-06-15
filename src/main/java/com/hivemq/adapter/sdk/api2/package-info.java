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
 * SDK v2 — the contracts a protocol adapter author implements, with no dependency on Edge internals.
 * <p>
 * <b>Mechanism vs. policy.</b> A {@link com.hivemq.adapter.sdk.api2.ProtocolAdapter2} is <b>pure
 * mechanism</b>: it executes commands and reports state and events through its
 * {@link com.hivemq.adapter.sdk.api2.model.ProtocolAdapterOutput2}. It has ZERO retry, backoff, reconnect, or
 * scheduling logic — the controlling framework owns all policy. Every command is asynchronous and acknowledged
 * by a reported event.
 * <p>
 * <b>The messaging contract.</b> The output is a tell-façade: each call is one thread-safe
 * <i>tell</i> onto the controlling message handler's mailbox — a multi-producer / single-consumer priority
 * queue (see {@link com.hivemq.adapter.sdk.api2.messaging}). Delivery is by message-type priority
 * ({@code CONTROL} &gt; {@code EVENT} &gt; {@code TICK} &gt; {@code DATA}), FIFO within a band. Adapter code
 * may call any output method from any thread with no locking.
 * <p>
 * <b>The reuse boundary (decision D2).</b> The protocol-agnostic v1 SDK subset is reused as-is and never
 * duplicated here: values are {@link com.hivemq.adapter.sdk.api.data.DataPoint} (built with
 * {@link com.hivemq.adapter.sdk.api.factories.DataPointFactory}), value and node-definition shapes are
 * {@link com.hivemq.adapter.sdk.api.schema.Schema} (the new-in-v2 adapter-configuration schema is
 * {@link com.hivemq.adapter.sdk.api2.schema.AdapterConfigSchema}, deliberately not the reused {@code Schema}),
 * browse node kinds are {@link com.hivemq.adapter.sdk.api.discovery.NodeType}, and adapter metadata uses
 * {@link com.hivemq.adapter.sdk.api.ProtocolAdapterCategory} and
 * {@link com.hivemq.adapter.sdk.api.ProtocolAdapterTag}. SDK v1 is not modified.
 * <p>
 * <b>Naming rules.</b> N1: {@code 2} is a suffix, never an infix — a v2 type with a v1 counterpart is the v1
 * name plus a trailing {@code 2}; a genuinely-new type takes no {@code 2}. N2: no abbreviations in any API
 * name, method signature, or variable name.
 */
package com.hivemq.adapter.sdk.api2;
