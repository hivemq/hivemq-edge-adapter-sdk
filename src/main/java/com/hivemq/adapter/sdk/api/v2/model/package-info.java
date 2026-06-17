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
 * SDK v2 model types: the adapter's construction input and its state/event output, plus the value types that
 * cross the command/event boundary.
 * <p>
 * {@link com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterInput} carries everything one adapter instance is
 * built from (its identifier, typed configuration, Node/Tag pairs, and framework services);
 * {@link com.hivemq.adapter.sdk.api.v2.model.ProtocolAdapterOutput} is the adapter's thread-safe tell-façade back
 * to the framework. The boundary value types — {@link com.hivemq.adapter.sdk.api.v2.model.WriteEntry},
 * {@link com.hivemq.adapter.sdk.api.v2.model.BrowseFilter},
 * {@link com.hivemq.adapter.sdk.api.v2.model.BrowseResultEntry},
 * {@link com.hivemq.adapter.sdk.api.v2.model.ErrorScope}, and
 * {@link com.hivemq.adapter.sdk.api.v2.model.VerifyOutcome} — reuse the v1 types where they exist (a southbound
 * value is the reused {@link com.hivemq.adapter.sdk.api.data.DataPoint}; a browse entry's node kind is the
 * reused {@link com.hivemq.adapter.sdk.api.discovery.NodeType}). (Mirrors v1's
 * {@code com.hivemq.adapter.sdk.api.model}.)
 */
package com.hivemq.adapter.sdk.api.v2.model;
