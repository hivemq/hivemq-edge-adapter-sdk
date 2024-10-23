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
package com.hivemq.adapter.sdk.api.services;

import com.hivemq.adapter.sdk.api.tag.Tag;
import org.jetbrains.annotations.NotNull;

public interface ProtocolAdapterTagService {

    <T> @NotNull Tag<T> resolveTag(final @NotNull String tagName, final @NotNull Class<T> addressClass);

    @NotNull
    AddStatus addTag(@NotNull String adapterId, @NotNull String protocolId, @NotNull Tag<?> tag);


    enum AddStatus {
        SUCCESS,
        ALREADY_PRESENT
    }
}
