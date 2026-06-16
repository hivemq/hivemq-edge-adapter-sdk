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
 * The generic messaging runtime — type-safe, thread-safe primitives with no framework dependency.
 * <p>
 * <b>The messaging contract.</b> A {@link com.hivemq.adapter.sdk.api2.messaging.MessageHandler} is private
 * state plus a single-threaded behavior
 * ({@link com.hivemq.adapter.sdk.api2.messaging.MessageHandler#receive(com.hivemq.adapter.sdk.api2.messaging.MailboxMessage)
 * receive}) fed one message at a time from a {@link com.hivemq.adapter.sdk.api2.messaging.Mailbox} by a
 * pluggable {@link com.hivemq.adapter.sdk.api2.messaging.MessageDispatcher}. Components interact <b>only</b> by
 * telling each other typed, immutable messages — there is no ask, no blocking on futures inside a handler, and
 * no shared mutable state. Producers hold only the send-only
 * {@link com.hivemq.adapter.sdk.api2.messaging.MailboxSender}: they cannot poll, cannot read handler state, and
 * cannot reach the handler itself.
 * <p>
 * <b>The mailbox is a queue, not a consumer-invoker.</b> It is multi-producer / single-consumer:
 * {@code tell} is safe from any thread; {@code poll}, {@code awaitNextMessage}, {@code isEmpty}, and
 * {@code size} belong exclusively to the one dispatch thread the dispatcher runs the handler on.
 * <p>
 * <b>The priority ladder.</b> Delivery is by message-type priority —
 * {@link com.hivemq.adapter.sdk.api2.messaging.MailboxMessagePriority#CONTROL CONTROL} &gt;
 * {@link com.hivemq.adapter.sdk.api2.messaging.MailboxMessagePriority#EVENT EVENT} &gt;
 * {@link com.hivemq.adapter.sdk.api2.messaging.MailboxMessagePriority#TICK TICK} &gt;
 * {@link com.hivemq.adapter.sdk.api2.messaging.MailboxMessagePriority#DATA DATA} — and FIFO within each band: a
 * producer's emit order is its delivery order inside a band. The band is declared once, on the message type,
 * never per instance.
 * <p>
 * {@link com.hivemq.adapter.sdk.api2.messaging.DefaultMailbox} is the SDK's concrete mailbox, so that adapter
 * templates need no framework dependency; the framework runtime reuses it for its own message handlers.
 */
package com.hivemq.adapter.sdk.api2.messaging;
