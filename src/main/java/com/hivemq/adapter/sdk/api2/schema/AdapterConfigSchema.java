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
package com.hivemq.adapter.sdk.api2.schema;

/**
 * The schema describing an adapter type's instance configuration.
 * <p>
 * This is a <b>new v2 schema type</b>, deliberately distinct from the reused v1
 * {@link com.hivemq.adapter.sdk.api.schema.Schema}: that type describes data points and node values, which is
 * the wrong abstraction for adapter configuration. This interface is an <b>empty placeholder</b> in this
 * project; its concrete shape is defined later.
 */
public interface AdapterConfigSchema {
}
