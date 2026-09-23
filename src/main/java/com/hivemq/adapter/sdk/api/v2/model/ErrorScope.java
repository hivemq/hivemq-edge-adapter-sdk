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
package com.hivemq.adapter.sdk.api.v2.model;

/**
 * The scope of an adapter-reported error: it decides which recovery the framework attempts.
 */
public enum ErrorScope {
    /**
     * The adapter itself failed — not recoverable by reconnecting.
     */
    ADAPTER,
    /**
     * The connection failed — recoverable by the framework's reconnect policy.
     */
    CONNECTION
}
