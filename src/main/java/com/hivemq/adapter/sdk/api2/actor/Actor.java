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
package com.hivemq.adapter.sdk.api2.actor;

import org.jetbrains.annotations.NotNull;

/**
 * The behavior bound to a {@link Mailbox}. {@link #receive(Message)} handles exactly one message at a time and
 * is never invoked concurrently: the actor's own state therefore needs no locks, as long as it is touched only
 * from inside {@code receive}.
 *
 * @param <MessageType> the message type this actor handles.
 */
public interface Actor<MessageType extends Message> {

    /**
     * Handle exactly one message. Invoked by the {@link Dispatcher} on the actor's single dispatch thread,
     * never concurrently.
     *
     * @param message the next message, drained from the mailbox in priority-band order.
     */
    void receive(@NotNull MessageType message);
}
