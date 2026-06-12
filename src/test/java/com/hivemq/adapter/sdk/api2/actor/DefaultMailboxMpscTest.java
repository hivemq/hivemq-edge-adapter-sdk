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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * The multi-producer / single-consumer contract of {@link DefaultMailbox} on REAL threads — Awaitility only,
 * never {@code Thread.sleep}. (Scenario S23 at the SDK level.)
 */
class DefaultMailboxMpscTest {

    private static final int PRODUCER_COUNT = 8;
    private static final int MESSAGES_PER_PRODUCER = 250;

    private record TestMessage(int producerIndex, int sequence, @NotNull MessagePriority band)
            implements Message {
        @Override
        public @NotNull MessagePriority priority() {
            return band;
        }
    }

    @Test
    void concurrentProducers_allMessagesReceivedExactlyOnce_perProducerFifoWithinEachBand() throws Exception {
        final DefaultMailbox<TestMessage> mailbox = new DefaultMailbox<>();
        final int expectedTotal = PRODUCER_COUNT * MESSAGES_PER_PRODUCER;
        final MessagePriority[] priorities = MessagePriority.values();

        final List<TestMessage> received = new ArrayList<>(expectedTotal);
        final AtomicBoolean consumerDone = new AtomicBoolean(false);
        final Thread consumer = new Thread(() -> {
            try {
                while (received.size() < expectedTotal) {
                    final TestMessage message = mailbox.awaitNextMessage(10_000);
                    if (message != null) {
                        received.add(message);
                    }
                }
            } catch (final InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
            consumerDone.set(true);
        }, "mailbox-consumer");
        consumer.start();

        final CountDownLatch startSignal = new CountDownLatch(1);
        final List<Thread> producers = new ArrayList<>(PRODUCER_COUNT);
        for (int producerIndex = 0; producerIndex < PRODUCER_COUNT; producerIndex++) {
            final int fixedProducerIndex = producerIndex;
            final Thread producer = new Thread(() -> {
                try {
                    startSignal.await();
                } catch (final InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int sequence = 0; sequence < MESSAGES_PER_PRODUCER; sequence++) {
                    final MessagePriority band = priorities[(fixedProducerIndex + sequence) % priorities.length];
                    mailbox.tell(new TestMessage(fixedProducerIndex, sequence, band));
                }
            }, "mailbox-producer-" + producerIndex);
            producers.add(producer);
            producer.start();
        }
        startSignal.countDown();

        await().atMost(30, TimeUnit.SECONDS).untilTrue(consumerDone);
        for (final Thread producer : producers) {
            producer.join(TimeUnit.SECONDS.toMillis(10));
        }
        consumer.join(TimeUnit.SECONDS.toMillis(10));

        // exactly N x M received — no loss, no duplication
        assertThat(received).hasSize(expectedTotal);
        final Set<String> distinctMessageKeys = new HashSet<>();
        for (final TestMessage message : received) {
            distinctMessageKeys.add(message.producerIndex() + ":" + message.sequence());
        }
        assertThat(distinctMessageKeys).hasSize(expectedTotal);

        // per-producer FIFO WITHIN each priority band: a producer's emit order is its delivery order
        final Map<Integer, EnumMap<MessagePriority, List<Integer>>> sequencesByProducerAndBand = new HashMap<>();
        for (final TestMessage message : received) {
            sequencesByProducerAndBand
                    .computeIfAbsent(message.producerIndex(), key -> new EnumMap<>(MessagePriority.class))
                    .computeIfAbsent(message.band(), key -> new ArrayList<>())
                    .add(message.sequence());
        }
        for (final EnumMap<MessagePriority, List<Integer>> sequencesByBand : sequencesByProducerAndBand.values()) {
            for (final List<Integer> sequences : sequencesByBand.values()) {
                assertThat(sequences).isSorted().doesNotHaveDuplicates();
            }
        }

        assertThat(mailbox.isEmpty()).isTrue();
        assertThat(mailbox.size()).isZero();
    }

    @Test
    void awaitNextMessage_wakesOnTellFromAnotherThread() throws Exception {
        final DefaultMailbox<TestMessage> mailbox = new DefaultMailbox<>();
        final AtomicReference<TestMessage> receivedMessage = new AtomicReference<>();
        final AtomicBoolean done = new AtomicBoolean(false);

        final Thread consumer = new Thread(() -> {
            try {
                receivedMessage.set(mailbox.awaitNextMessage(TimeUnit.SECONDS.toMillis(30)));
            } catch (final InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
            done.set(true);
        }, "blocked-consumer");
        consumer.start();

        // wait until the consumer is parked inside awaitNextMessage, then tell
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> consumer.getState() == Thread.State.TIMED_WAITING ||
                        consumer.getState() == Thread.State.WAITING);
        final TestMessage message = new TestMessage(0, 0, MessagePriority.EVENT);
        mailbox.tell(message);

        await().atMost(10, TimeUnit.SECONDS).untilTrue(done);
        consumer.join(TimeUnit.SECONDS.toMillis(10));
        assertThat(receivedMessage.get()).isSameAs(message);
    }

    @Test
    void awaitNextMessage_timesOutCleanly() throws Exception {
        final DefaultMailbox<TestMessage> mailbox = new DefaultMailbox<>();

        assertThat(mailbox.awaitNextMessage(0)).isNull();
        assertThat(mailbox.awaitNextMessage(20)).isNull();
        assertThat(Thread.currentThread().isInterrupted()).isFalse();

        // the mailbox stays fully usable after a timeout
        final TestMessage message = new TestMessage(0, 0, MessagePriority.EVENT);
        mailbox.tell(message);
        assertThat(mailbox.awaitNextMessage(0)).isSameAs(message);
    }
}
