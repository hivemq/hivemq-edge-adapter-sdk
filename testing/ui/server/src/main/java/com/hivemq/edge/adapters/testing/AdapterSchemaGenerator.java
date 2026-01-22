/*
 * Copyright 2019-present HiveMQ GmbH
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
package com.hivemq.edge.adapters.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.FieldScope;
import com.github.victools.jsonschema.generator.MemberScope;
import com.github.victools.jsonschema.generator.MethodScope;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfig;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigPart;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import com.hivemq.adapter.sdk.api.annotations.ModuleConfigField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Generates JSON Schema from adapter configuration classes.
 * <p>
 * This class processes {@link ModuleConfigField} annotations to generate
 * JSON Schema with proper validation constraints, titles, descriptions,
 * and custom attributes.
 * <p>
 * <strong>Note:</strong> This is a copy of CustomConfigSchemaGenerator from hivemq-edge core.
 * This decision was made to keep the testing module self-contained without requiring
 * a dependency on hivemq-edge. If schema generation logic changes in hivemq-edge,
 * this class should be updated accordingly.
 *
 * @see ModuleConfigField
 */
public class AdapterSchemaGenerator {

    public static final String ENUM_NAMES_ATTRIBUTE = "enumNames";

    /**
     * Generates a JSON Schema for the given configuration class.
     *
     * @param clazz the configuration class to generate schema for
     * @return the generated JSON Schema as a JsonNode
     */
    public @NotNull JsonNode generateJsonSchema(final @NotNull Class<?> clazz) {
        final SchemaGeneratorConfigBuilder configBuilder =
                new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
                        .with(new JacksonModule(
                                JacksonOption.RESPECT_JSONPROPERTY_REQUIRED,
                                JacksonOption.INCLUDE_ONLY_JSONPROPERTY_ANNOTATED_METHODS,
                                JacksonOption.RESPECT_JSONPROPERTY_ORDER))
                        .with(new ModuleConfigSchemaGeneratorModule());
        withEnumDisplayNameProvider(configBuilder);
        final SchemaGeneratorConfig config = configBuilder.build();
        final SchemaGenerator generator = new SchemaGenerator(config);
        return generator.generateSchema(clazz);
    }

    /**
     * Configures enum display names from the enumDisplayValues attribute.
     * These are exposed as "enumNames" in the schema for RJSF to display
     * user-friendly labels instead of raw enum values.
     */
    private static void withEnumDisplayNameProvider(final @NotNull SchemaGeneratorConfigBuilder configBuilder) {
        configBuilder.forFields().withInstanceAttributeOverride((collectedMemberAttributes, member, context) -> {
            final ModuleConfigField configField = member.getAnnotation(ModuleConfigField.class);
            if (configField != null) {
                final String[] displayValues = configField.enumDisplayValues();
                if (displayValues != null && displayValues.length > 0) {
                    ArrayNode node = (ArrayNode) collectedMemberAttributes.get(ENUM_NAMES_ATTRIBUTE);
                    if (node == null) {
                        node = collectedMemberAttributes.putArray(ENUM_NAMES_ATTRIBUTE);
                    }
                    Arrays.stream(displayValues).forEach(node::add);
                }
            }
        });
    }

    /**
     * Module that processes ModuleConfigField annotations and maps them
     * to JSON Schema attributes.
     */
    private static class ModuleConfigSchemaGeneratorModule implements Module {

        @Override
        public void applyToConfigBuilder(final @NotNull SchemaGeneratorConfigBuilder schemaGeneratorConfigBuilder) {
            applyToConfigPart(schemaGeneratorConfigBuilder.forFields());
            applyToConfigPart(schemaGeneratorConfigBuilder.forMethods());
        }

        private void applyToConfigPart(final @NotNull SchemaGeneratorConfigPart<?> schemaGeneratorConfigPart) {
            schemaGeneratorConfigPart
                    .withTitleResolver(this::title)
                    .withArrayMinItemsResolver(this::arrayMinItems)
                    .withArrayMaxItemsResolver(this::arrayMaxItems)
                    .withArrayUniqueItemsResolver(this::arrayUniqueItems)
                    .withDescriptionResolver(this::description)
                    .withStringFormatResolver(this::stringFormat)
                    .withDefaultResolver(this::defaultValue)
                    .withRequiredCheck(this::required)
                    .withReadOnlyCheck(this::readOnly)
                    .withWriteOnlyCheck(this::writeOnly)
                    .withNumberInclusiveMinimumResolver(this::numberInclusiveMin)
                    .withNumberInclusiveMaximumResolver(this::numberInclusiveMax)
                    .withStringMinLengthResolver(this::stringMinLength)
                    .withStringMaxLengthResolver(this::stringMaxLength)
                    .withStringPatternResolver(this::stringPattern)
                    .withIgnoreCheck(this::ignored)
                    .withInstanceAttributeOverride(this::customAttributes);
        }

        private void customAttributes(
                final @NotNull ObjectNode jsonNodes,
                final @NotNull MemberScope<?, ?> memberScope,
                final @NotNull SchemaGenerationContext schemaGenerationContext) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            if (fieldInfo == null || fieldInfo.customAttributes() == null || fieldInfo.customAttributes().length < 1) {
                return;
            }

            for (final ModuleConfigField.CustomAttribute customAttribute : fieldInfo.customAttributes()) {
                jsonNodes.put(customAttribute.name(), customAttribute.value());
            }
        }

        private boolean writeOnly(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.writeOnly();
        }

        private boolean readOnly(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.readOnly();
        }

        private Boolean arrayUniqueItems(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.arrayUniqueItems() ? true : null;
        }

        private Integer arrayMinItems(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.arrayMinItems() > 0 ? fieldInfo.arrayMinItems() : null;
        }

        private Integer arrayMaxItems(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.arrayMaxItems() > 0 && fieldInfo.arrayMaxItems() < Integer.MAX_VALUE ?
                    fieldInfo.arrayMaxItems() :
                    null;
        }

        private String title(final @NotNull MemberScope<?, ?> memberScope) {
            // Do not allow duplication of titles for wrapped types
            if (memberScope.isFakeContainerItemScope()) {
                return null;
            }
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.title() != null && !fieldInfo.title().isBlank() ?
                    fieldInfo.title() :
                    null;
        }

        private String description(final @NotNull MemberScope<?, ?> memberScope) {
            // Do not allow duplication of descriptions for wrapped types
            if (memberScope.isFakeContainerItemScope()) {
                return null;
            }
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.description() != null && !fieldInfo.description().isBlank() ?
                    fieldInfo.description() :
                    null;
        }

        private String stringFormat(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null &&
                    fieldInfo.format() != null &&
                    fieldInfo.format() != ModuleConfigField.FieldType.UNSPECIFIED ?
                    fieldInfo.format().getName() :
                    null;
        }

        private Object defaultValue(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            final String str = fieldInfo != null && fieldInfo.defaultValue() != null && !fieldInfo.defaultValue().isEmpty() ?
                    fieldInfo.defaultValue() :
                    null;
            return getNativeObject(str);
        }

        private boolean required(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.required();
        }

        private boolean ignored(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.ignore();
        }

        private BigDecimal numberInclusiveMin(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.numberMin() != Double.MIN_VALUE ?
                    BigDecimal.valueOf(fieldInfo.numberMin()) :
                    null;
        }

        private BigDecimal numberInclusiveMax(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.numberMax() < Double.MAX_VALUE ?
                    BigDecimal.valueOf(fieldInfo.numberMax()) :
                    null;
        }

        private Integer stringMinLength(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.stringMinLength() > 0 ? fieldInfo.stringMinLength() : null;
        }

        private Integer stringMaxLength(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null &&
                    fieldInfo.stringMaxLength() > 0 &&
                    fieldInfo.stringMaxLength() < Integer.MAX_VALUE ?
                    fieldInfo.stringMaxLength() :
                    null;
        }

        protected String stringPattern(final @NotNull MemberScope<?, ?> memberScope) {
            final ModuleConfigField fieldInfo = getModuleFieldInfo(memberScope);
            return fieldInfo != null && fieldInfo.stringPattern() != null && !fieldInfo.stringPattern().isBlank() ?
                    fieldInfo.stringPattern() :
                    null;
        }

        protected @Nullable ModuleConfigField getModuleFieldInfo(final @NotNull MemberScope<?, ?> member) {
            ModuleConfigField annotation = member.getAnnotation(ModuleConfigField.class);
            if (annotation == null) {
                MemberScope<?, ?> source;
                if (member instanceof FieldScope) {
                    source = ((FieldScope) member).findGetter();
                } else if (member instanceof MethodScope) {
                    source = ((MethodScope) member).findGetterField();
                } else {
                    source = null;
                }
                annotation = source == null ? null : source.getAnnotation(ModuleConfigField.class);
            }
            return annotation;
        }
    }

    private static Object getNativeObject(final @Nullable String format) {
        if (format != null) {
            if ("true".equalsIgnoreCase(format.trim()) ||
                    "false".equalsIgnoreCase(format.trim())) {
                return Boolean.parseBoolean(format.trim());
            }
            try {
                return Long.parseLong(format);
            } catch (final NumberFormatException e) {
                // Not a long, try double
            }
            try {
                return Double.parseDouble(format);
            } catch (final NumberFormatException e) {
                // Not a double, return as string
            }
        }
        return format;
    }
}
