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
package com.hivemq.adapter.sdk.api.schema.impl;

import com.hivemq.adapter.sdk.api.schema.Schema;
import org.jetbrains.annotations.Nullable;

/**
 * Holds the structure-definition state for a builder: which kind was defined,
 * the nullable flag, and the kind-specific data.
 */
class SchemaStructure {

    enum Kind {
        ANY,
        ANY_DEFAULT,
        SCALAR,
        OBJECT,
        ARRAY,
        SCHEMA
    }

    @Nullable
    Kind kind = null;

    boolean nullable = false;

    @Nullable
    ScalarSchemaBuilder scalarBuilder;

    @Nullable
    ObjectSchemaBuilderImpl<?> objectBuilder;

    @Nullable
    ArraySchemaBuilderImpl<?> arrayBuilder;

    @Nullable
    Schema prebuiltSchema;
}
