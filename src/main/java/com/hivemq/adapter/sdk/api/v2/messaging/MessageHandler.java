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
package com.hivemq.adapter.sdk.api.v2.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * The behavior bound to a {@link Mailbox}. {@link #receive(MailboxMessage)} handles exactly one message at a
 * time and is never invoked concurrently: the handler's own state therefore needs no locks, as long as it is
 * touched only from inside {@code receive}.
 *
 * @param <MessageType> the message type this handler handles.
 */
public interface MessageHandler<MessageType extends MailboxMessage> {

    /**
     * Handle exactly one message. Invoked by the {@link MessageDispatcher} on the handler's single dispatch
     * thread, never concurrently.
     *
     * @param message the next message, drained from the mailbox in priority-band order.
     */
    void receive(@NotNull MessageType message);
}
