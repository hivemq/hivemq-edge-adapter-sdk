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
/**
 * The protocol adapter template — the adapter as a single-threaded message handler, with no framework
 * dependency.
 * <p>
 * {@link com.hivemq.adapter.sdk.api2.template.AbstractProtocolAdapter2} turns the asynchronous
 * {@link com.hivemq.adapter.sdk.api2.ProtocolAdapter2} command interface into single-threaded {@code do*}
 * methods: each command method {@code tell}s one immutable
 * {@link com.hivemq.adapter.sdk.api2.messaging.ProtocolAdapterCommand} onto the adapter's own
 * {@link com.hivemq.adapter.sdk.api2.messaging.DefaultMailbox}, and the
 * {@link com.hivemq.adapter.sdk.api2.messaging.MessageDispatcher} supplied by the framework feeds them to
 * {@code receive} one at a time. An author implements the {@code do*} methods and never thinks about threads.
 * <p>
 * <b>Bands.</b> Lifecycle commands ({@code Start}/{@code Stop}/{@code Connect}/{@code Disconnect}) travel in
 * the {@code CONTROL} band; batch and browse commands travel in the {@code DATA} band — a stop or disconnect
 * is never starved behind a queued batch backlog, and FIFO within each band preserves the framework's emit
 * order.
 */
package com.hivemq.adapter.sdk.api2.template;
