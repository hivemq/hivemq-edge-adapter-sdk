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

/**
 * Marker for anything that may be placed in a {@link Mailbox}. Implementations MUST be immutable: ownership
 * transfers with the {@link MailboxSender#tell(MailboxMessage) tell}, and the message is read on a different
 * thread than the one that created it.
 * <p>
 * This is an ordinary, non-sealed interface on purpose: each handler's sealed message hierarchy lives in that
 * handler's own package (a {@code sealed} interface and its {@code permits} subtypes must share a package) and
 * extends this marker across packages freely.
 */
public interface MailboxMessage {

    /**
     * The delivery band — a property of the type, never of the instance. Defaults to
     * {@link MailboxMessagePriority#EVENT}.
     *
     * @return the {@link MailboxMessagePriority} band this message type is delivered in.
     */
    default @NotNull MailboxMessagePriority priority() {
        return MailboxMessagePriority.EVENT;
    }
}
