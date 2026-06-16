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
import org.jetbrains.annotations.Nullable;

/**
 * A multi-producer / single-consumer PRIORITY queue. NOT a consumer-invoker: the mailbox is just a queue — it
 * never takes a consumer and never invokes a handler. A pluggable {@link MessageDispatcher} drains it and feeds
 * the {@link MessageHandler}'s {@code receive} one message at a time.
 * <p>
 * {@link #tell(MailboxMessage)} is safe from any thread; all other methods are called ONLY by the owning
 * dispatch thread.
 * <p>
 * <b>ORDERING CONTRACT:</b> delivery is by message-type priority ({@link MailboxMessagePriority#CONTROL} &gt;
 * {@link MailboxMessagePriority#EVENT} &gt; {@link MailboxMessagePriority#TICK} &gt;
 * {@link MailboxMessagePriority#DATA}); WITHIN one band the mailbox is FIFO — a producer's emit order is its
 * delivery order inside that band. All protocol adapter lifecycle events share the {@code EVENT} band, which is
 * what makes "a late {@code connected()} cannot overtake the {@code disconnected()} that followed it" true by
 * construction.
 *
 * @param <MessageType> the message type carried by this mailbox.
 */
public interface Mailbox<MessageType extends MailboxMessage> extends MailboxSender<MessageType> {

    /**
     * Owner thread only. Non-blocking.
     *
     * @return the highest-priority message available, or {@code null} when the mailbox is empty.
     */
    @Nullable MessageType poll();

    /**
     * Blocking receive for thread-per-handler dispatchers. Owner thread only.
     * <p>
     * This is the wakeup contract: a {@link #tell(MailboxMessage)} from any thread releases a waiting owner.
     *
     * @param timeoutMillis how long to wait for a message before giving up, in milliseconds.
     * @return the highest-priority message available, or {@code null} on timeout.
     * @throws InterruptedException if the owning dispatch thread is interrupted while waiting.
     */
    @Nullable MessageType awaitNextMessage(long timeoutMillis) throws InterruptedException;

    /**
     * Owner thread only.
     *
     * @return whether every priority band is empty.
     */
    boolean isEmpty();

    /**
     * Owner thread only.
     *
     * @return the approximate number of queued messages, summed across all priority bands; feeds the mailbox
     *         depth metric.
     */
    int size();
}
