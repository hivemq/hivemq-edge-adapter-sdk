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
package com.hivemq.adapter.sdk.api2;

import com.hivemq.adapter.sdk.api.ProtocolAdapterCategory;
import com.hivemq.adapter.sdk.api.ProtocolAdapterTag;
import com.hivemq.adapter.sdk.api.data.DataPoint;
import com.hivemq.adapter.sdk.api.discovery.NodeType;
import com.hivemq.adapter.sdk.api.factories.DataPointFactory;
import com.hivemq.adapter.sdk.api.schema.Schema;
import com.hivemq.adapter.sdk.api2.model.BrowseResultEntry;
import com.hivemq.adapter.sdk.api2.model.WriteEntry;
import com.hivemq.adapter.sdk.api2.factories.ProtocolAdapterFactory2;
import com.hivemq.adapter.sdk.api2.node.Tag2;
import com.hivemq.adapter.sdk.api2.services.ProtocolAdapterService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The reuse boundary (decision D2) and the naming rules (N1/N2): {@code api2} references the reused v1 types
 * — {@link DataPoint}, {@link Schema}, {@link NodeType}, the category and search-tag enums — and never shadows
 * them; no new identifier carries an infix {@code 2} or a known abbreviation.
 */
class ReuseBoundaryTest {

    private static final Path API2_SOURCE_DIRECTORY =
            Path.of("src", "main", "java", "com", "hivemq", "adapter", "sdk", "api2");

    /**
     * Simple names of reused v1 types — a same-named type under {@code api2} would be a fork.
     */
    private static final Set<String> REUSED_TYPE_SIMPLE_NAMES = Set.of(
            "DataPoint",
            "DataPointFactory",
            "Schema",
            "ScalarSchema",
            "ScalarType",
            "NodeType",
            "ProtocolAdapterCategory",
            "ProtocolAdapterTag");

    /**
     * Architectural shorthands that must never appear in code identifiers (naming rule N2).
     */
    private static final Set<String> FORBIDDEN_ABBREVIATIONS =
            Set.of("Paw", "Pam", "Paf", "Fsm", "Ctx", "Cfg", "Ack", "Mgr");

    @Test
    void api2_referencesTheReusedV1Types() throws Exception {
        assertThat(Tag2.class.getMethod("schema").getReturnType()).isEqualTo(Schema.class);
        assertThat(WriteEntry.class.getMethod("value").getReturnType()).isEqualTo(DataPoint.class);
        assertThat(BrowseResultEntry.class.getMethod("type").getReturnType()).isEqualTo(NodeType.class);
        assertThat(ProtocolAdapterService.class.getMethod("dataPointFactory").getReturnType())
                .isEqualTo(DataPointFactory.class);
        assertThat(ProtocolAdapterFactory2.class.getMethod("adapterConfigSchema").getReturnType())
                .isEqualTo(Schema.class);
        assertThat(ProtocolAdapterFactory2.class.getMethod("nodeDefinitionSchema").getReturnType())
                .isEqualTo(Schema.class);
        assertThat(ProtocolAdapterInformation2.class.getMethod("category").getReturnType())
                .isEqualTo(ProtocolAdapterCategory.class);

        final Type tagsReturnType = ProtocolAdapterInformation2.class.getMethod("tags").getGenericReturnType();
        assertThat(tagsReturnType).isInstanceOf(ParameterizedType.class);
        final ParameterizedType parameterizedTagsReturnType = (ParameterizedType) tagsReturnType;
        assertThat(parameterizedTagsReturnType.getRawType()).isEqualTo(List.class);
        assertThat(parameterizedTagsReturnType.getActualTypeArguments())
                .containsExactly(ProtocolAdapterTag.class);
    }

    @Test
    void api2_doesNotShadowAnyReusedType() throws IOException {
        for (final String typeName : api2TopLevelTypeNames()) {
            assertThat(REUSED_TYPE_SIMPLE_NAMES)
                    .as("api2 type %s must not fork a reused v1 type", typeName)
                    .doesNotContain(typeName);
        }
    }

    @Test
    void namingRuleN1_twoIsASuffixNeverAnInfix() throws IOException {
        for (final String typeName : api2TopLevelTypeNames()) {
            if (typeName.indexOf('2') >= 0) {
                assertThat(typeName)
                        .as("identifier %s must carry '2' only as a suffix (naming rule N1)", typeName)
                        .matches("[A-Za-z]+2");
            }
        }
    }

    @Test
    void namingRuleN2_noAbbreviationsInTypeNames() throws IOException {
        for (final String typeName : api2TopLevelTypeNames()) {
            for (final String abbreviation : FORBIDDEN_ABBREVIATIONS) {
                assertThat(typeName)
                        .as("identifier %s must not contain the abbreviation '%s' (naming rule N2)",
                                typeName, abbreviation)
                        .doesNotContain(abbreviation);
            }
        }
    }

    private static List<String> api2TopLevelTypeNames() throws IOException {
        assertThat(API2_SOURCE_DIRECTORY)
                .as("the test must run with the project directory as its working directory")
                .exists();
        try (final Stream<Path> paths = Files.walk(API2_SOURCE_DIRECTORY)) {
            return paths.filter(path -> path.getFileName().toString().endsWith(".java"))
                    .map(path -> path.getFileName().toString().replace(".java", ""))
                    .filter(typeName -> !typeName.equals("package-info"))
                    .toList();
        }
    }
}
