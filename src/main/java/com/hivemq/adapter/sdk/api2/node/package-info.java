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
 * The Node/Tag pair model — the inseparable pair at the heart of the v2 data model.
 * <p>
 * {@link com.hivemq.adapter.sdk.api2.node.Node} is the protocol-specific half: a passive data object,
 * subclassed per protocol, and the ONLY thing a protocol adapter ever sees.
 * {@link com.hivemq.adapter.sdk.api2.node.Tag2} is Edge's half. The two are created together via
 * {@link com.hivemq.adapter.sdk.api2.node.NodeTagPair} and each holds a direct reference to the other —
 * correlation across the adapter boundary is by {@code Node} reference, never by name lookup, and there is no
 * lookup map anywhere.
 * <p>
 * A tag's value shape is the <b>reused</b> v1 {@link com.hivemq.adapter.sdk.api.schema.Schema} — no v2
 * duplicate exists (reuse boundary, decision D2).
 */
package com.hivemq.adapter.sdk.api2.node;
