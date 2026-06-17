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
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * EDG-734 — independent, adversarial concurrency verification of {@link DefaultMailbox}, deliberately disjoint
 * from {@code DefaultMailboxMpscTest} (which already proves exactly-once delivery, within-band FIFO, a single
 * wake-on-tell, and clean timeout). This class attacks only what that test leaves open, exclusively on REAL
 * threads, Awaitility only — never {@code Thread.sleep}:
 * <ol>
 *   <li>strict priority extraction over a backlog built by <i>concurrent</i> producers across every band;</li>
 *   <li>a lost-wakeup hammer — every {@code tell} is delivered onto a consumer verified to be parked inside
 *       {@link DefaultMailbox#awaitNextMessage(long)}, repeated thousands of times;</li>
 *   <li>interrupt of a parked consumer: {@link InterruptedException} is raised, the lock is released, and the
 *       same mailbox stays fully usable with no message lost;</li>
 *   <li>sustained multi-band load drained across a mid-run consumer stall (queue growth + visibility), with
 *       exactly-once and within-band per-producer FIFO preserved;</li>
 *   <li>cross-lock memory visibility: a <i>non-volatile</i> field written before {@code tell} is observed by
 *       the consumer after {@code poll} — the lock's release/acquire is the only happens-before edge.</li>
 * </ol>
 * It exercises {@link DefaultMailbox} directly and never through a dispatcher: the dispatcher is the
 * single-consumer <i>precondition</i>, not the unit under verification here.
 */
class DefaultMailboxAdversarialConcurrencyTest {

    private static final @NotNull MailboxMessagePriority @NotNull [] BANDS = MailboxMessagePriority.values();
    private static final @NotNull MailboxMessagePriority @NotNull [] NON_CONTROL_BANDS = nonControlBands();
    private static final int SENTINEL = 0x5AFE5AFE;

    private static void assertBandsAreInNonIncreasingPriorityOrder(final @NotNull List<SeqMessage> drained) {
        int highestSeenOrdinal = 0; // CONTROL == 0; ordinal grows as priority falls
        for (final SeqMessage message : drained) {
            final int ordinal = message.band().ordinal();
            assertThat(ordinal).as(
                            "a higher-priority band surfaced after a lower one — priority extraction is not strict")
                    .isGreaterThanOrEqualTo(highestSeenOrdinal);
            highestSeenOrdinal = ordinal;
        }
    }

    private static void assertWithinBandPerProducerFifo(final @NotNull List<SeqMessage> drained) {
        final Map<Integer, EnumMap<MailboxMessagePriority, List<Integer>>> byProducerThenBand = new HashMap<>();
        for (final SeqMessage message : drained) {
            byProducerThenBand.computeIfAbsent(message.producer(), key -> new EnumMap<>(MailboxMessagePriority.class))
                    .computeIfAbsent(message.band(), key -> new ArrayList<>())
                    .add(message.sequence());
        }
        for (final EnumMap<MailboxMessagePriority, List<Integer>> byBand : byProducerThenBand.values()) {
            for (final List<Integer> sequences : byBand.values()) {
                assertThat(sequences).isSorted().doesNotHaveDuplicates();
            }
        }
    }

    private static boolean isParked(final @NotNull Thread thread) {
        final Thread.State state = thread.getState();
        return state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING;
    }

    private static void awaitLatch(final @NotNull CountDownLatch latch) {
        try {
            latch.await();
        } catch (final @NotNull InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while awaiting latch", interrupted);
        }
    }

    private static void joinAll(final @NotNull List<Thread> threads) throws InterruptedException {
        for (final Thread thread : threads) {
            thread.join(TimeUnit.SECONDS.toMillis(30));
            assertThat(thread.isAlive()).as("thread %s did not finish", thread.getName()).isFalse();
        }
    }

    private static @NotNull MailboxMessagePriority @NotNull [] nonControlBands() {
        return Arrays.stream(MailboxMessagePriority.values())
                .filter(priority -> priority != MailboxMessagePriority.CONTROL)
                .toArray(MailboxMessagePriority[]::new);
    }

    @Test
    void priorityExtraction_isStrictOverABacklogBuiltByConcurrentProducers() throws Exception {
        final DefaultMailbox<SeqMessage> mailbox = new DefaultMailbox<>();
        final int producerCount = 8;
        final int messagesPerProducer = 2_000;
        final int backlogTotal = producerCount * messagesPerProducer;

        // N producers race to fill the mailbox; each producer owns one NON-control band so that within-band
        // per-producer FIFO is a meaningful invariant despite the concurrent interleave.
        final CountDownLatch startSignal = new CountDownLatch(1);
        final List<Thread> producers = new ArrayList<>(producerCount);
        for (int p = 0; p < producerCount; p++) {
            final int producerIndex = p;
            final MailboxMessagePriority band = NON_CONTROL_BANDS[p % NON_CONTROL_BANDS.length];
            final Thread producer = new Thread(() -> {
                awaitLatch(startSignal);
                for (int sequence = 0; sequence < messagesPerProducer; sequence++) {
                    mailbox.tell(new SeqMessage(producerIndex, sequence, band));
                }
            }, "fill-producer-" + p);
            producers.add(producer);
            producer.start();
        }
        startSignal.countDown();
        joinAll(producers);

        // the whole backlog is now statically enqueued; a single CONTROL message is told LAST of all.
        mailbox.tell(new SeqMessage(-1, 0, MailboxMessagePriority.CONTROL));

        final List<SeqMessage> drained = new ArrayList<>(backlogTotal + 1);
        for (SeqMessage message = mailbox.poll(); message != null; message = mailbox.poll()) {
            drained.add(message);
        }

        assertThat(drained).hasSize(backlogTotal + 1);
        // the CONTROL told dead last still comes out first.
        assertThat(drained.get(0).band()).isEqualTo(MailboxMessagePriority.CONTROL);
        // strict priority: once a lower band appears, no higher band may ever follow.
        assertBandsAreInNonIncreasingPriorityOrder(drained);
        // and within each (producer, band) the emission order survived the concurrent fill.
        assertWithinBandPerProducerFifo(drained);
        assertThat(mailbox.isEmpty()).isTrue();
    }

    @Test
    void everyTellOntoAParkedConsumer_wakesIt_neverLosingASignal() throws Exception {
        final DefaultMailbox<SeqMessage> mailbox = new DefaultMailbox<>();
        final int rounds = 1_000;

        final AtomicInteger received = new AtomicInteger(0);
        final AtomicReference<Throwable> consumerFailure = new AtomicReference<>();
        final Thread consumer = new Thread(() -> {
            try {
                while (received.get() < rounds) {
                    final SeqMessage message = mailbox.awaitNextMessage(TimeUnit.SECONDS.toMillis(30));
                    if (message != null) {
                        received.incrementAndGet();
                    }
                }
            } catch (final InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            } catch (final Throwable failure) {
                consumerFailure.set(failure);
            }
        }, "parked-consumer");
        consumer.start();

        for (int round = 0; round < rounds; round++) {
            final int expectedBefore = round;
            // wait until the consumer has caught up AND re-parked, so this tell is guaranteed to hit a
            // blocked awaitNextMessage and must rely on signal() to make progress. Tight poll: the default
            // 100ms interval would impose a ~100s floor over thousands of rounds.
            await().pollDelay(Duration.ZERO)
                    .pollInterval(Duration.ofMillis(1))
                    .atMost(10, TimeUnit.SECONDS)
                    .until(() -> received.get() == expectedBefore && isParked(consumer));
            mailbox.tell(new SeqMessage(0, round, MailboxMessagePriority.EVENT));
        }

        // if a single signal were ever lost, the consumer would block forever and this would time out.
        await().atMost(20, TimeUnit.SECONDS).until(() -> received.get() == rounds);
        consumer.join(TimeUnit.SECONDS.toMillis(10));
        assertThat(consumerFailure.get()).isNull();
        assertThat(received.get()).isEqualTo(rounds);
        assertThat(mailbox.isEmpty()).isTrue();
    }

    @Test
    void interruptingAParkedConsumer_throws_andLeavesTheMailboxFullyUsable() throws Exception {
        final DefaultMailbox<SeqMessage> mailbox = new DefaultMailbox<>();
        // a message told BEFORE the interrupt must not be lost by the interrupt unwinding the lock.
        final SeqMessage survivor = new SeqMessage(7, 7, MailboxMessagePriority.DATA);
        mailbox.tell(survivor);

        final AtomicBoolean sawInterrupt = new AtomicBoolean(false);
        final AtomicReference<Throwable> unexpected = new AtomicReference<>();
        final Thread consumer = new Thread(() -> {
            try {
                // drain the survivor, then park on an empty mailbox waiting to be interrupted.
                assertThat(mailbox.awaitNextMessage(TimeUnit.SECONDS.toMillis(10))).isSameAs(survivor);
                mailbox.awaitNextMessage(TimeUnit.SECONDS.toMillis(30));
            } catch (final InterruptedException interrupted) {
                sawInterrupt.set(true);
            } catch (final Throwable failure) {
                unexpected.set(failure);
            }
        }, "interruptible-consumer");
        consumer.start();

        await().atMost(10, TimeUnit.SECONDS).until(() -> isParked(consumer));
        consumer.interrupt();
        consumer.join(TimeUnit.SECONDS.toMillis(10));

        assertThat(unexpected.get()).isNull();
        assertThat(sawInterrupt.get()).as("parked consumer must observe the interrupt").isTrue();

        // the lock was released on the way out: the mailbox is still correct and live on this thread.
        final SeqMessage afterControl = new SeqMessage(1, 1, MailboxMessagePriority.CONTROL);
        final SeqMessage afterData = new SeqMessage(2, 2, MailboxMessagePriority.DATA);
        mailbox.tell(afterData);
        mailbox.tell(afterControl);
        assertThat(mailbox.size()).isEqualTo(2);
        assertThat(mailbox.poll()).isSameAs(afterControl); // priority intact
        assertThat(mailbox.poll()).isSameAs(afterData);
        assertThat(mailbox.isEmpty()).isTrue();
    }

    @Test
    void sustainedLoadAcrossAConsumerStall_deliversExactlyOnce_preservingWithinBandFifo() throws Exception {
        final DefaultMailbox<SeqMessage> mailbox = new DefaultMailbox<>();
        final int producerCount = 12;
        final int messagesPerProducer = 5_000;
        final int expectedTotal = producerCount * messagesPerProducer;

        final List<SeqMessage> received = new ArrayList<>(expectedTotal); // single consumer → no sync needed
        final AtomicBoolean consumerDone = new AtomicBoolean(false);
        final CountDownLatch firstReceived = new CountDownLatch(1);
        final CountDownLatch resumeConsumer = new CountDownLatch(1);
        final AtomicReference<Throwable> consumerFailure = new AtomicReference<>();

        final Thread consumer = new Thread(() -> {
            try {
                // take exactly one, then stall while producers keep flooding — forcing real queue growth.
                final SeqMessage first = mailbox.awaitNextMessage(TimeUnit.SECONDS.toMillis(10));
                if (first != null) {
                    received.add(first);
                }
                firstReceived.countDown();
                awaitLatch(resumeConsumer);
                while (received.size() < expectedTotal) {
                    final SeqMessage message = mailbox.awaitNextMessage(TimeUnit.SECONDS.toMillis(20));
                    if (message != null) {
                        received.add(message);
                    }
                }
            } catch (final InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            } catch (final Throwable failure) {
                consumerFailure.set(failure);
            }
            consumerDone.set(true);
        }, "stalling-consumer");
        consumer.start();

        final CountDownLatch startSignal = new CountDownLatch(1);
        final List<Thread> producers = new ArrayList<>(producerCount);
        for (int p = 0; p < producerCount; p++) {
            final int producerIndex = p;
            final Thread producer = new Thread(() -> {
                awaitLatch(startSignal);
                for (int sequence = 0; sequence < messagesPerProducer; sequence++) {
                    final MailboxMessagePriority band = BANDS[(producerIndex + sequence) % BANDS.length];
                    mailbox.tell(new SeqMessage(producerIndex, sequence, band));
                }
            }, "load-producer-" + p);
            producers.add(producer);
            producer.start();
        }
        startSignal.countDown();

        // hold the consumer stalled until the first message is in hand and producers have all finished,
        // guaranteeing a large backlog accumulated behind a stationary consumer before the drain resumes.
        await().atMost(10, TimeUnit.SECONDS).until(() -> firstReceived.getCount() == 0);
        joinAll(producers);
        resumeConsumer.countDown();

        await().atMost(30, TimeUnit.SECONDS).untilTrue(consumerDone);
        consumer.join(TimeUnit.SECONDS.toMillis(10));

        assertThat(consumerFailure.get()).isNull();
        assertThat(received).hasSize(expectedTotal);
        final Set<String> distinct = new HashSet<>();
        for (final SeqMessage message : received) {
            distinct.add(message.producer() + ":" + message.sequence());
        }
        assertThat(distinct).as("exactly-once: no loss, no duplication").hasSize(expectedTotal);
        assertWithinBandPerProducerFifo(received);
        assertThat(mailbox.isEmpty()).isTrue();
        assertThat(mailbox.size()).isZero();
    }

    @Test
    void nonVolatilePayloadWrittenBeforeTell_isAlwaysVisibleAfterPoll() throws Exception {
        final DefaultMailbox<VisibilityMessage> mailbox = new DefaultMailbox<>();
        final int producerCount = 6;
        final int messagesPerProducer = 20_000;
        final int expectedTotal = producerCount * messagesPerProducer;

        final AtomicLong staleReads = new AtomicLong(0);
        final AtomicInteger consumed = new AtomicInteger(0);
        final AtomicReference<Throwable> consumerFailure = new AtomicReference<>();
        final Thread consumer = new Thread(() -> {
            try {
                while (consumed.get() < expectedTotal) {
                    final VisibilityMessage message = mailbox.awaitNextMessage(TimeUnit.SECONDS.toMillis(30));
                    if (message != null) {
                        // the producer wrote payload = SENTINEL before tell; the lock must make it visible.
                        if (message.payload != SENTINEL) {
                            staleReads.incrementAndGet();
                        }
                        consumed.incrementAndGet();
                    }
                }
            } catch (final InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            } catch (final Throwable failure) {
                consumerFailure.set(failure);
            }
        }, "visibility-consumer");
        consumer.start();

        final CountDownLatch startSignal = new CountDownLatch(1);
        final List<Thread> producers = new ArrayList<>(producerCount);
        for (int p = 0; p < producerCount; p++) {
            final Thread producer = new Thread(() -> {
                awaitLatch(startSignal);
                for (int i = 0; i < messagesPerProducer; i++) {
                    final VisibilityMessage message = new VisibilityMessage(MailboxMessagePriority.DATA);
                    message.payload = SENTINEL; // plain write, happens-before the tell's lock release
                    mailbox.tell(message);
                }
            }, "visibility-producer-" + p);
            producers.add(producer);
            producer.start();
        }
        startSignal.countDown();
        joinAll(producers);

        await().atMost(30, TimeUnit.SECONDS).until(() -> consumed.get() == expectedTotal);
        consumer.join(TimeUnit.SECONDS.toMillis(10));

        assertThat(consumerFailure.get()).isNull();
        assertThat(consumed.get()).isEqualTo(expectedTotal);
        assertThat(staleReads.get()).as("the lock must establish happens-before for the plain field").isZero();
        assertThat(mailbox.isEmpty()).isTrue();
    }

    private record SeqMessage(int producer, int sequence, @NotNull MailboxMessagePriority band)
            implements MailboxMessage {
        @Override
        public @NotNull MailboxMessagePriority priority() {
            return band;
        }
    }

    /**
     * A message whose {@code payload} is a PLAIN (non-volatile, non-final) field: it is written before the
     * {@code tell} and read after the {@code poll}, so only the mailbox lock can make the write visible.
     */
    private static final class VisibilityMessage implements MailboxMessage {
        private final @NotNull MailboxMessagePriority band;
        @SuppressWarnings("unused")
        private int payload;

        private VisibilityMessage(final @NotNull MailboxMessagePriority band) {
            this.band = band;
        }

        @Override
        public @NotNull MailboxMessagePriority priority() {
            return band;
        }
    }
}
