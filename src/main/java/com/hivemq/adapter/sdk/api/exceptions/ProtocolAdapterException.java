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
package com.hivemq.adapter.sdk.api.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * A generic exception to signal problems within the protocol adapter routines.
 * Users are free to extends this class to have more specialised exceptions for their needs.
 */
public class ProtocolAdapterException extends Exception {

    public ProtocolAdapterException() {
    }

    public ProtocolAdapterException(final @NotNull String message) {
        super(message);
    }

    public ProtocolAdapterException(final @NotNull String message, final @NotNull Throwable cause) {
        super(message, cause);
    }

    public ProtocolAdapterException(final @NotNull Throwable cause) {
        super(cause);
    }
}
