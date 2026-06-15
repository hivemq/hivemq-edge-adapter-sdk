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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default multi-producer / single-consumer priority mailbox. It lives in the SDK so an adapter template needs
 * no framework dependency; the framework runtime reuses it for its own message handlers.
 * <p>
 * Implementation: one unbounded FIFO queue per {@link MailboxMessagePriority} band behind one lock plus
 * condition — the lock gives a correct blocking take across bands and cross-thread memory visibility; the lock
 * cost is acceptable at the expected message rates. A lock-free per-band variant behind the same
 * {@link Mailbox} interface is a documented performance extension point.
 * <p>
 * Thread-safety follows the {@link Mailbox} contract: {@link #tell(MailboxMessage)} from any thread;
 * {@link #poll()}, {@link #awaitNextMessage(long)}, {@link #isEmpty()}, and {@link #size()} only from the
 * owning dispatch thread.
 *
 * @param <MessageType> the message type carried by this mailbox.
 */
public final class DefaultMailbox<MessageType extends MailboxMessage> implements Mailbox<MessageType> {

    private static final @NotNull MailboxMessagePriority @NotNull [] PRIORITIES = MailboxMessagePriority.values();

    private final @NotNull ReentrantLock lock = new ReentrantLock();
    private final @NotNull Condition notEmpty = lock.newCondition();
    private final @NotNull List<ArrayDeque<MessageType>> bands;

    public DefaultMailbox() {
        final List<ArrayDeque<MessageType>> mutableBands = new ArrayList<>(PRIORITIES.length);
        for (int i = 0; i < PRIORITIES.length; i++) {
            mutableBands.add(new ArrayDeque<>());
        }
        bands = List.copyOf(mutableBands);
    }

    @Override
    public void tell(final @NotNull MessageType message) {
        lock.lock();
        try {
            bands.get(message.priority().ordinal()).addLast(message);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public @Nullable MessageType poll() {
        lock.lock();
        try {
            return pollHighestPriority();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public @Nullable MessageType awaitNextMessage(final long timeoutMillis) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            long remainingNanos = TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
            while (true) {
                final MessageType message = pollHighestPriority();
                if (message != null) {
                    return message;
                }
                if (remainingNanos <= 0L) {
                    return null;
                }
                remainingNanos = notEmpty.awaitNanos(remainingNanos);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            for (final ArrayDeque<MessageType> band : bands) {
                if (!band.isEmpty()) {
                    return false;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            int sum = 0;
            for (final ArrayDeque<MessageType> band : bands) {
                sum += band.size();
            }
            return sum;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Must be called with {@link #lock} held.
     */
    private @Nullable MessageType pollHighestPriority() {
        for (final ArrayDeque<MessageType> band : bands) {
            final MessageType message = band.pollFirst();
            if (message != null) {
                return message;
            }
        }
        return null;
    }
}
