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
package com.hivemq.adapter.sdk.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

/**
 * Marks a POJO whose listed sibling JSON properties are mutually exclusive: at most one may be set per instance.
 * <p>
 * The JSON schema generator rewrites the type's schema into a top-level {@code oneOf} with one branch per member
 * (and optionally a "none set" default branch). This is consumed by React JSON Schema Form in the Edge frontend
 * as a labelled picker that reveals only the selected branch's fields.
 * <p>
 * The backend still enforces mutual exclusion via its own constructor / validation. This annotation only shapes
 * the emitted JSON schema; runtime deserialization is unaffected.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MutuallyExclusiveFields {

    /**
     * JSON property names (matching {@code @JsonProperty} values) of the mutually exclusive sibling fields.
     * Order determines picker order.
     */
    @NotNull String[] value();

    /**
     * Per-branch human-readable titles, parallel to {@link #value()}. When an entry is empty or absent the
     * corresponding JSON property name is used as the title.
     */
    @NotNull String[] titles() default {};

    /**
     * When {@code true}, emit an additional {@code oneOf} branch representing "none set" — matching instances
     * where none of the listed properties are present. Use for optional-default semantics.
     */
    boolean includeDefault() default false;

    /**
     * Title for the default branch. Ignored when {@link #includeDefault()} is {@code false}.
     */
    @NotNull String defaultTitle() default "Default";

    /**
     * Optional top-level {@code title} for the rewritten object schema, shown by the picker above the options.
     * When empty, no top-level title override is applied.
     */
    @NotNull String groupTitle() default "";
}
