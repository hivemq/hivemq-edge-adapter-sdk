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
package com.hivemq.adapter.sdk.api.config.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This interface is used to support conversions from the old format into the new format.
 *
 * Its primary purpose is to give edge access to the tags which so far have been part of the mapping definitions.
 *
 * @deprecated this class will be removed in early 2025, there won't be a replacement.
 */
public interface LegacyConfigConversion {

    @NotNull
    ConfigTagsTuple tryConvertLegacyConfig(@NotNull ObjectMapper objectMapper, @NotNull Map<String, Object> config);
}
