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
package com.hivemq.adapter.sdk.api.v2.messaging.command;

import com.hivemq.adapter.sdk.api.v2.ProtocolAdapter;
import com.hivemq.adapter.sdk.api.v2.messaging.MailboxMessage;
import com.hivemq.adapter.sdk.api.v2.messaging.MailboxMessagePriority;

/**
 * The template adapter's mailbox messages — one per {@link ProtocolAdapter} command. Sealed; immutable
 * records (list components are defensively copied).
 * <p>
 * <b>Bands.</b> {@link ProtocolAdapterConnectionCommand} commands are {@link MailboxMessagePriority#CONTROL} — a
 * stop or disconnect is never starved behind a queued batch backlog; {@link ProtocolAdapterBatchProcessCommand}
 * commands are {@link MailboxMessagePriority#DATA} — bulk work yields to lifecycle. FIFO within each band
 * preserves the framework's emit order. (Nothing can preempt an in-flight {@code do*} — the bands order only
 * what is still queued.)
 */
public sealed interface ProtocolAdapterCommand extends MailboxMessage
        permits ProtocolAdapterConnectionCommand, ProtocolAdapterBatchProcessCommand {
}
