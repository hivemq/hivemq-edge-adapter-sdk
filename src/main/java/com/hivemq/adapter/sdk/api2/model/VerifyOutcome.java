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
package com.hivemq.adapter.sdk.api2.model;

import org.jetbrains.annotations.NotNull;

/**
 * The outcome of verifying one node against the connected device. Sealed: exactly these three outcomes exist,
 * so consumers switch exhaustively without a {@code default} branch.
 */
public sealed interface VerifyOutcome {

    /**
     * The node was verified successfully.
     */
    record Success() implements VerifyOutcome {
    }

    /**
     * Verification failed for a reason that may resolve by itself — the framework retries after a delay.
     *
     * @param reason a human-readable description of the failure.
     */
    record TransientFailure(@NotNull String reason) implements VerifyOutcome {
    }

    /**
     * Verification failed permanently — the framework suspends the tag until a user-commanded retry or a
     * configuration fix.
     *
     * @param reason a human-readable description of the failure.
     */
    record PermanentFailure(@NotNull String reason) implements VerifyOutcome {
    }
}
