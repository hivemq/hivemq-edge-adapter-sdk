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
 * Binds a {@link Mailbox} to a {@link MessageHandler} and pumps messages. The dispatcher drains the mailbox and
 * feeds {@code receive} one message at a time — the mailbox itself never invokes anything.
 * <p>
 * Pluggable: production implementations use a dedicated thread per handler that blocks in
 * {@link Mailbox#awaitNextMessage(long)}; test implementations drain deterministically on the calling thread.
 */
public interface MessageDispatcher {

    /**
     * Bind the given mailbox to the given handler and start pumping messages.
     *
     * @param mailbox       the mailbox to drain; its owner-thread-only methods become the dispatcher's to
     *                      call.
     * @param handler       the behavior to feed, one message at a time, never concurrently.
     * @param <MessageType> the message type shared by mailbox and handler.
     * @return a handle that stops the pumping when closed.
     */
    <MessageType extends MailboxMessage> @NotNull MessageDispatcherHandle attach(
            @NotNull Mailbox<MessageType> mailbox,
            @NotNull MessageHandler<MessageType> handler);
}
