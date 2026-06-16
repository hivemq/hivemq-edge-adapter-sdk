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
package com.hivemq.adapter.sdk.api2.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Send-only, thread-safe handle — the "tell" capability. This is the ONLY capability producers hold: a
 * producer cannot poll, cannot read handler state, and cannot reach the {@link MessageHandler}.
 *
 * @param <MessageType> the message type accepted by the underlying mailbox.
 */
public interface MailboxSender<MessageType extends MailboxMessage> {

    /**
     * Enqueue one message. Thread-safe, non-blocking, fire-and-forget: callable from ANY thread.
     *
     * @param message the immutable message to enqueue; ownership transfers with the call.
     */
    void tell(@NotNull MessageType message);
}
