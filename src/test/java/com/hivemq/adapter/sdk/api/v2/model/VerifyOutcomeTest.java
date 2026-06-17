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
package com.hivemq.adapter.sdk.api.v2.model;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link VerifyOutcome} is sealed with exactly three record permits — consumers switch exhaustively without a
 * {@code default} branch (this test class is itself the compile-time proof).
 */
class VerifyOutcomeTest {

    @Test
    void sealedHierarchy_permitsExactlyThreeRecords() {
        assertThat(VerifyOutcome.class.isSealed()).isTrue();
        assertThat(VerifyOutcome.class.getPermittedSubclasses()).containsExactlyInAnyOrder(
                VerifyOutcome.Success.class,
                VerifyOutcome.TransientFailure.class,
                VerifyOutcome.PermanentFailure.class);
        assertThat(VerifyOutcome.Success.class.isRecord()).isTrue();
        assertThat(VerifyOutcome.TransientFailure.class.isRecord()).isTrue();
        assertThat(VerifyOutcome.PermanentFailure.class.isRecord()).isTrue();
    }

    @Test
    void exhaustiveSwitchWithoutDefault_coversAllOutcomes() {
        assertThat(describe(new VerifyOutcome.Success())).isEqualTo("success");
        assertThat(describe(new VerifyOutcome.TransientFailure("flaky link")))
                .isEqualTo("transient: flaky link");
        assertThat(describe(new VerifyOutcome.PermanentFailure("node does not exist")))
                .isEqualTo("permanent: node does not exist");
    }

    /**
     * Compiles WITHOUT a {@code default} branch — the sealed permits are the whole universe.
     */
    private static @NotNull String describe(final @NotNull VerifyOutcome outcome) {
        return switch (outcome) {
            case VerifyOutcome.Success success -> "success";
            case VerifyOutcome.TransientFailure transientFailure -> "transient: " + transientFailure.reason();
            case VerifyOutcome.PermanentFailure permanentFailure -> "permanent: " + permanentFailure.reason();
        };
    }
}
