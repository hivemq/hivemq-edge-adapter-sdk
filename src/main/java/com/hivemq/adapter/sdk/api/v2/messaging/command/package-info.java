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
 * The protocol-adapter command messages — one sealed mailbox-message type per
 * {@link com.hivemq.adapter.sdk.api.v2.ProtocolAdapter} command.
 * <p>
 * {@link com.hivemq.adapter.sdk.api.v2.messaging.command.ProtocolAdapterCommand} is the sealed set the
 * {@link com.hivemq.adapter.sdk.api.v2.template.AbstractProtocolAdapter template} tells onto its own mailbox,
 * split into the {@link com.hivemq.adapter.sdk.api.v2.messaging.command.ProtocolAdapterConnectionCommand}
 * ({@code CONTROL}) and {@link com.hivemq.adapter.sdk.api.v2.messaging.command.ProtocolAdapterBatchProcessCommand}
 * ({@code DATA}) bands. The value types those commands and their acknowledgements carry — write entries, browse
 * filters and results, error scopes, and verification outcomes — live in
 * {@link com.hivemq.adapter.sdk.api.v2.model}.
 */
package com.hivemq.adapter.sdk.api.v2.messaging.command;
