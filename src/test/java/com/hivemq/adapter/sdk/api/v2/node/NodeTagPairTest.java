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
package com.hivemq.adapter.sdk.api.v2.node;

import com.hivemq.adapter.sdk.api.schema.ScalarSchema;
import com.hivemq.adapter.sdk.api.schema.ScalarType;
import com.hivemq.adapter.sdk.api.schema.Schema;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The inseparable pair: created together, cross-referencing, carrying the reused v1 {@link Schema}.
 */
class NodeTagPairTest {

    private static final class TestNode extends Node {
        @Override
        public @NotNull String nodeId() {
            return "test-node";
        }

        @Override
        public @NotNull String nodeString() {
            return "{\"identifier\":\"test-node\"}";
        }

        @Override
        public @NotNull EnumSet<NodeProperty> properties() {
            return EnumSet.of(NodeProperty.UNIQUE, NodeProperty.TYPED, NodeProperty.VALID);
        }
    }

    @Test
    void create_buildsTheCrossReferencingPair() {
        final TestNode node = new TestNode();
        final Schema schema = new ScalarSchema(ScalarType.STRING, null, null, null, null, false, true, false);

        final NodeTagPair pair = NodeTagPair.create(node, "temperature", schema, true, false);

        assertThat(pair.node()).isSameAs(node);
        assertThat(pair.tag().node()).isSameAs(node);
        assertThat(pair.tag().name()).isEqualTo("temperature");
        assertThat(pair.tag().pollable()).isTrue();
        assertThat(pair.tag().subscribable()).isFalse();
    }

    @Test
    void tag_carriesThePassedReusedSchema() {
        final Schema schema = new ScalarSchema(ScalarType.DOUBLE, 0, 100, null, null, false, true, true);

        final NodeTagPair pair = NodeTagPair.create(new TestNode(), "pressure", schema, false, true);

        assertThat(pair.tag().schema()).isSameAs(schema);
    }

    @Test
    void nodePropertyQuery_delegatesToComputedProperties() {
        final TestNode node = new TestNode();

        assertThat(node.is(NodeProperty.UNIQUE)).isTrue();
        assertThat(node.is(NodeProperty.TYPED)).isTrue();
        assertThat(node.is(NodeProperty.VALID)).isTrue();
    }

    @Test
    void pairHalves_haveNoPublicConstructors() {
        assertThat(Tag.class.getConstructors()).isEmpty();
        assertThat(NodeTagPair.class.getConstructors()).isEmpty();
    }
}
