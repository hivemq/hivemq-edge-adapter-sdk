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

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The mailbox is a QUEUE, not a consumer-invoker: it exposes only {@code tell} / {@code poll} /
 * {@code awaitNextMessage} / {@code isEmpty} / {@code size} and no method that accepts a consumer, handler, or
 * message handler. The {@link MessageDispatcher} drains, never the mailbox. (Reflection assertion, guarding
 * against the consumer-invoker anti-pattern sneaking back in.)
 */
class MailboxContractTest {

    private static final Set<String> ALLOWED_METHOD_NAMES =
            Set.of("tell", "poll", "awaitNextMessage", "isEmpty", "size");

    @Test
    void mailboxInterface_exposesOnlyTheQueueContract() {
        final Set<String> methodNames = Arrays.stream(Mailbox.class.getMethods())
                .map(Method::getName)
                .collect(Collectors.toSet());
        assertThat(methodNames).isEqualTo(ALLOWED_METHOD_NAMES);
    }

    @Test
    void mailboxInterface_hasNoConsumerAcceptingMethod() {
        for (final Method method : Mailbox.class.getMethods()) {
            for (final Class<?> parameterType : method.getParameterTypes()) {
                assertThat(parameterType.getPackageName())
                        .as("method %s must not accept a functional callback", method.getName())
                        .isNotEqualTo("java.util.function");
                assertThat(parameterType)
                        .as("method %s must not accept a MessageHandler — the MessageDispatcher drains, never " +
                                "the mailbox", method.getName())
                        .isNotEqualTo(MessageHandler.class);
            }
        }
    }

    @Test
    void defaultMailbox_addsNoPublicMethodsBeyondTheContract() {
        final Set<String> declaredPublicMethodNames = Arrays.stream(DefaultMailbox.class.getMethods())
                .filter(method -> method.getDeclaringClass() == DefaultMailbox.class)
                .map(Method::getName)
                .collect(Collectors.toSet());
        assertThat(ALLOWED_METHOD_NAMES).containsAll(declaredPublicMethodNames);
    }

    @Test
    void mailboxSender_isTheSendOnlySlice() {
        final Set<String> methodNames = Arrays.stream(MailboxSender.class.getMethods())
                .map(Method::getName)
                .collect(Collectors.toSet());
        assertThat(methodNames).containsExactly("tell");
        assertThat(MailboxSender.class).isAssignableFrom(Mailbox.class);
    }
}
