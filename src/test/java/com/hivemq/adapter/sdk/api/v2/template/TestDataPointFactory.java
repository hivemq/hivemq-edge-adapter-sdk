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
package com.hivemq.adapter.sdk.api.v2.template;

import com.hivemq.adapter.sdk.api.data.DataPoint;
import com.hivemq.adapter.sdk.api.factories.DataPointFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The reused v1 {@link DataPointFactory} contract, backed by a simple immutable record — the SDK ships only
 * the interface; the production implementation lives in the framework.
 */
final class TestDataPointFactory implements DataPointFactory {

    record TestDataPoint(@NotNull String tagName, @NotNull Object tagValue, boolean json) implements DataPoint {
        @Override
        public @NotNull Object getTagValue() {
            return tagValue;
        }

        @Override
        public boolean treatTagValueAsJson() {
            return json;
        }

        @Override
        public @NotNull String getTagName() {
            return tagName;
        }
    }

    @Override
    public @NotNull DataPoint create(final @NotNull String tagName, final @NotNull Object tagValue) {
        return new TestDataPoint(tagName, tagValue, false);
    }

    @Override
    public @NotNull DataPoint createJsonDataPoint(final @NotNull String tagName, final @NotNull Object tagValue) {
        return new TestDataPoint(tagName, tagValue, true);
    }
}
