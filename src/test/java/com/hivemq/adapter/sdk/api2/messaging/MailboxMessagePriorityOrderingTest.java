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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The priority ladder — {@code CONTROL} &gt; {@code EVENT} &gt; {@code TICK} &gt; {@code DATA}, FIFO within a
 * band — observed through {@link DefaultMailbox#poll()}.
 */
class MailboxMessagePriorityOrderingTest {

    private record TestMessage(@NotNull String label, @NotNull MailboxMessagePriority band)
            implements MailboxMessage {
        @Override
        public @NotNull MailboxMessagePriority priority() {
            return band;
        }
    }

    @Test
    void controlToldAfterDataBacklog_isPolledFirst() {
        final DefaultMailbox<TestMessage> mailbox = new DefaultMailbox<>();
        for (int i = 0; i < 100; i++) {
            mailbox.tell(new TestMessage("data-" + i, MailboxMessagePriority.DATA));
        }
        mailbox.tell(new TestMessage("control", MailboxMessagePriority.CONTROL));

        final TestMessage first = mailbox.poll();
        assertThat(first).isNotNull();
        assertThat(first.label()).isEqualTo("control");
    }

    @Test
    void eventToldAfterTick_isPolledBeforeIt() {
        final DefaultMailbox<TestMessage> mailbox = new DefaultMailbox<>();
        mailbox.tell(new TestMessage("tick", MailboxMessagePriority.TICK));
        mailbox.tell(new TestMessage("event", MailboxMessagePriority.EVENT));

        final TestMessage first = mailbox.poll();
        final TestMessage second = mailbox.poll();
        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first.label()).isEqualTo("event");
        assertThat(second.label()).isEqualTo("tick");
    }

    @Test
    void fullLadder_toldInReverse_isPolledHighestPriorityFirst() {
        final DefaultMailbox<TestMessage> mailbox = new DefaultMailbox<>();
        mailbox.tell(new TestMessage("data", MailboxMessagePriority.DATA));
        mailbox.tell(new TestMessage("tick", MailboxMessagePriority.TICK));
        mailbox.tell(new TestMessage("event", MailboxMessagePriority.EVENT));
        mailbox.tell(new TestMessage("control", MailboxMessagePriority.CONTROL));

        assertThat(pollLabels(mailbox, 4)).containsExactly("control", "event", "tick", "data");
        assertThat(mailbox.isEmpty()).isTrue();
    }

    @Test
    void equalPriorityMessages_comeOutInTellOrder() {
        final DefaultMailbox<TestMessage> mailbox = new DefaultMailbox<>();
        mailbox.tell(new TestMessage("first", MailboxMessagePriority.EVENT));
        mailbox.tell(new TestMessage("second", MailboxMessagePriority.EVENT));
        mailbox.tell(new TestMessage("third", MailboxMessagePriority.EVENT));

        assertThat(pollLabels(mailbox, 3)).containsExactly("first", "second", "third");
    }

    @Test
    void priority_defaultsToEvent() {
        final MailboxMessage message = new MailboxMessage() {
        };
        assertThat(message.priority()).isEqualTo(MailboxMessagePriority.EVENT);
    }

    @Test
    void sizeAndIsEmpty_sumAcrossBands() {
        final DefaultMailbox<TestMessage> mailbox = new DefaultMailbox<>();
        assertThat(mailbox.isEmpty()).isTrue();
        assertThat(mailbox.size()).isZero();

        mailbox.tell(new TestMessage("control", MailboxMessagePriority.CONTROL));
        mailbox.tell(new TestMessage("data", MailboxMessagePriority.DATA));
        assertThat(mailbox.isEmpty()).isFalse();
        assertThat(mailbox.size()).isEqualTo(2);

        mailbox.poll();
        assertThat(mailbox.size()).isEqualTo(1);
        mailbox.poll();
        assertThat(mailbox.isEmpty()).isTrue();
        assertThat(mailbox.poll()).isNull();
    }

    private static @NotNull String[] pollLabels(final @NotNull DefaultMailbox<TestMessage> mailbox, final int count) {
        final String[] labels = new String[count];
        for (int i = 0; i < count; i++) {
            final TestMessage message = mailbox.poll();
            assertThat(message).isNotNull();
            labels[i] = message.label();
        }
        return labels;
    }
}
