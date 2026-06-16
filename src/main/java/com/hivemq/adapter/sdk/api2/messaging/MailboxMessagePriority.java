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

/**
 * The mailbox delivery bands, highest priority first.
 * <p>
 * The band is a property of the <b>message type</b> — every message type maps to exactly one band; the ladder,
 * not producer timing, decides cross-band delivery order. Within one band the mailbox is FIFO (see
 * {@link Mailbox}).
 * <p>
 * Why each band sits where it does:
 * <ul>
 * <li>{@link #CONTROL} above everything — operator and supervisor intent is never starved by device traffic.
 * Jumping the queue is always safe by construction: goal and lifecycle commands are valid in every state of the
 * receiving state machine and bypass its transition table, so early delivery can never cause a defensive
 * reset.</li>
 * <li>{@link #EVENT} above {@link #TICK} — an acknowledgment already enqueued is always processed (and its
 * watchdog canceled) before the tick that would have fired that watchdog. A watchdog therefore fires only when
 * the awaited reply truly has not arrived.</li>
 * <li>{@link #TICK} above {@link #DATA} — time stays on time under data floods: watchdogs, backoff, poll
 * schedules, and batch dispatch are never pushed behind a chatty device's backlog of data points.</li>
 * <li>{@link #DATA} lowest — the only high-volume band, and the one whose stale messages are harmless: state
 * machines absorb late data points and browse results in non-collecting states.</li>
 * </ul>
 */
public enum MailboxMessagePriority {
    /**
     * Goal and lifecycle commands — operator/supervisor intent. Always safe to deliver first: goal commands are
     * valid in every state and bypass the receiving state machine's transition table.
     */
    CONTROL,
    /**
     * State-machine events: acknowledgments, errors, verification and write results.
     */
    EVENT,
    /**
     * Tick messages — time itself, delivered as a message.
     */
    TICK,
    /**
     * Bulk payload: data points, browse results, batch work.
     */
    DATA
}
