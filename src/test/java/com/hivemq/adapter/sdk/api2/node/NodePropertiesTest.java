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
package com.hivemq.adapter.sdk.api2.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivemq.adapter.sdk.api2.command.BrowseFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The {@link Node} contract: {@link Node#properties()} is COMPUTED from the subclass's fields (never
 * serialized), the UNIQUE-implies-TYPED implication holds where the protocol implies it, and
 * {@link Node#nodeString()} is the Jackson serialization of the fields — an empty node is a valid browse
 * filter and still serializes a node string.
 */
class NodePropertiesTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * A protocol where an address that pins a node also pins its data type — so UNIQUE implies TYPED.
     * Properties are derived from the fields on every call; the computed accessors are not bean getters and
     * therefore never appear in the Jackson serialization.
     */
    private static final class AddressNode extends Node {

        public @Nullable String address;

        public AddressNode() {
        }

        private AddressNode(final @Nullable String address) {
            this.address = address;
        }

        @Override
        public @NotNull String nodeId() {
            return address == null ? "" : address;
        }

        @Override
        public @NotNull String nodeString() {
            try {
                return OBJECT_MAPPER.writeValueAsString(this);
            } catch (final JsonProcessingException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public @NotNull EnumSet<NodeProperty> properties() {
            if (address == null || address.isEmpty()) {
                return EnumSet.noneOf(NodeProperty.class);
            }
            // the protocol implies the type from the address: UNIQUE => TYPED
            return EnumSet.of(NodeProperty.UNIQUE, NodeProperty.TYPED, NodeProperty.VALID);
        }
    }

    @Test
    void properties_areDerivedFromTheFields() {
        final AddressNode addressed = new AddressNode("ns=2;s=Temperature");
        assertThat(addressed.properties())
                .containsExactlyInAnyOrder(NodeProperty.UNIQUE, NodeProperty.TYPED, NodeProperty.VALID);
        assertThat(addressed.is(NodeProperty.UNIQUE)).isTrue();

        final AddressNode unaddressed = new AddressNode(null);
        assertThat(unaddressed.properties()).isEmpty();
        assertThat(unaddressed.is(NodeProperty.UNIQUE)).isFalse();

        // properties follow the fields — computed on every call, not captured at construction time
        unaddressed.address = "ns=2;s=Pressure";
        assertThat(unaddressed.properties())
                .containsExactlyInAnyOrder(NodeProperty.UNIQUE, NodeProperty.TYPED, NodeProperty.VALID);
    }

    @Test
    void uniqueImpliesTyped_whereTheProtocolImpliesIt() {
        final AddressNode node = new AddressNode("ns=2;s=Temperature");
        assertThat(node.is(NodeProperty.UNIQUE)).isTrue();
        assertThat(node.is(NodeProperty.TYPED)).isTrue();
    }

    @Test
    void emptyNode_isAValidBrowseFilter_andSerializesANodeString() throws Exception {
        final AddressNode empty = new AddressNode();

        final BrowseFilter filter = new BrowseFilter(empty);
        assertThat(filter.filterNode()).isSameAs(empty);

        final JsonNode parsed = OBJECT_MAPPER.readTree(empty.nodeString());
        assertThat(parsed.isObject()).isTrue();
        // computed accessors are not bean getters — only fields are serialized
        assertThat(parsed.has("nodeId")).isFalse();
        assertThat(parsed.has("nodeString")).isFalse();
        assertThat(parsed.has("properties")).isFalse();
    }

    @Test
    void nodeString_roundTripsThroughJackson() throws Exception {
        final AddressNode original = new AddressNode("ns=2;s=Temperature");

        final AddressNode restored = OBJECT_MAPPER.readValue(original.nodeString(), AddressNode.class);

        assertThat(restored.address).isEqualTo(original.address);
        assertThat(restored.properties()).isEqualTo(original.properties());
        assertThat(restored.nodeString()).isEqualTo(original.nodeString());
    }
}
