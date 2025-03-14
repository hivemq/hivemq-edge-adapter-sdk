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
package com.hivemq.adapter.sdk.api.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enrich configuration fields with user facing information.
 * <p>
 * This information is used to display and validate the annotated configuration value in the API as well as the UI.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ModuleConfigField {

    /**
     * @return title of properties
     */
    @Nullable String title() default "";

    /**
     * @return Description of properties
     */
    @Nullable String description() default "";

    /**
     * @return Format for String properties
     */
    @Nullable FieldType format() default FieldType.UNSPECIFIED;

    /**
     * @return Default value of properties
     */
    @Nullable String defaultValue() default "";

    /**
     * @return MultipleOf value for Number properties
     */
    double multipleOf() default 0;

    /**
     * @return Minimum value for Number properties
     */
    double numberMin() default Double.MIN_VALUE;

    /**
     * @return Maximum value for Number properties
     */
    double numberMax() default Double.MAX_VALUE;

    /**
     * @return Minimum length for String properties
     */
    int stringMinLength() default 0;

    /**
     * @return Maximum length for String properties
     */
    int stringMaxLength() default Integer.MAX_VALUE;

    /**
     * @return Pattern for String properties
     */
    @Nullable String stringPattern() default "";

    /**
     * Use this on enum types if you wish to override the default display-names for a given enum value
     *
     * @return the displayed values for the enum
     */
    String @NotNull [] enumDisplayValues() default {};

    /**
     * @return boolean indicating whether the property must be set and may not be null
     */
    boolean required() default false;

    /**
     * Ignore field/ method during the process of generation of json schema
     *
     * @return true: the field/method will not be regarded when json are generated. false: They are part of the json generation.
     */
    boolean ignore() default false;

    /**
     * @return minimum items in array
     */
    int arrayMinItems() default 0;

    /**
     * @return maximum items in array
     */
    int arrayMaxItems() default Integer.MAX_VALUE;

    /**
     * @return field is read only via API
     */
    boolean readOnly() default false;

    /**
     * @return Field is write only via API
     */
    boolean writeOnly() default false;

    /**
     * @return array can only contain unique items, no duplicates
     */
    boolean arrayUniqueItems() default false;

    String @NotNull [] allowedValues() default {};

    /**
     * @return custom attributes added to schema info
     */
    CustomAttribute @NotNull [] customAttributes() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.ANNOTATION_TYPE})
    @interface CustomAttribute {
        @NotNull String name();

        @NotNull String value();
    }

    enum FieldType {

        //json schema built-in
        DATE("date"),
        TIME("time"),
        DATE_TIME("date-time"),
        REGEX("regex"),
        EMAIL("email"),
        HOSTNAME("hostname"),
        IPV4("ipv4"),
        IPV6("ipv6"),
        JSON_POINTER("json-pointer"),
        RELATIVE_JSON_POINTER("relative-json-pointer"),
        URI("uri"),

        //custom
        UNSPECIFIED("unspecified"),
        IDENTIFIER("identifier"),
        BOOLEAN("boolean"),
        MQTT_TOPIC("mqtt-topic"),
        MQTT_TOPIC_FILTER("mqtt-topic-filter"),
        MQTT_TAG("mqtt-tag");

        private final @NotNull String name;

        FieldType(final @NotNull String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return name;
        }
    }

}
