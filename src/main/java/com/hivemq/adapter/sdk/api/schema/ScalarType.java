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
package com.hivemq.adapter.sdk.api.schema;

/**
 * The primitive types that a {@link ScalarSchema} can represent.
 * <p>
 * {@code NULL} is not a scalar type — null-ability is expressed via {@link Schema#nullable()}.
 * <p>
 * Temporal types ({@link #INSTANT}, {@link #LOCAL_DATE}, {@link #LOCAL_TIME},
 * {@link #LOCAL_DATE_TIME}, {@link #DURATION}) are serialised as JSON Schema strings
 * with a {@code format} keyword — standard RFC 3339 / ISO 8601 where the format exists
 * ({@code date-time}, {@code date}, {@code duration}), and custom format strings where
 * RFC 3339 has no zoneless form ({@code local-time}, {@code local-date-time}).
 */
public enum ScalarType {
    BOOLEAN,
    LONG,
    ULONG,
    DOUBLE,
    STRING,
    BINARY,

    /**
     * A point on the UTC timeline, mapped to {@link java.time.Instant}.
     * <p>
     * Wire format: {@code {"type": "string", "format": "date-time"}} (RFC 3339 / ISO 8601),
     * always serialised with a {@code Z} offset.
     * <p>
     * Canonical long encoding: epoch-milliseconds (see {@link java.time.Instant#ofEpochMilli(long)}).
     * <p>
     * Primary producer: OPC UA {@code DateTime}.
     */
    INSTANT,

    /**
     * A calendar date without time, zone, or offset — mapped to {@link java.time.LocalDate}.
     * <p>
     * Wire format: {@code {"type": "string", "format": "date"}} (RFC 3339 full-date,
     * e.g. {@code 2024-01-15}).
     * <p>
     * Canonical long encoding: epoch-day
     * (see {@link java.time.LocalDate#ofEpochDay(long)}).
     * <p>
     * Primary producer: PLC4X {@code DATE}, {@code LDATE} (IEC 61131-3 zoneless dates).
     */
    LOCAL_DATE,

    /**
     * A time-of-day without date, zone, or offset — mapped to {@link java.time.LocalTime}.
     * <p>
     * Wire format: {@code {"type": "string", "format": "local-time"}} (custom; RFC 3339
     * {@code time} mandates an offset that IEC 61131-3 values do not carry). Validators
     * that don't recognise the format fall back to plain {@code type: string}.
     * <p>
     * Canonical long encoding: nanos-of-day
     * (see {@link java.time.LocalTime#ofNanoOfDay(long)}).
     * <p>
     * Primary producer: PLC4X {@code TIME_OF_DAY}, {@code LTIME_OF_DAY}.
     */
    LOCAL_TIME,

    /**
     * A date-and-time without zone or offset — mapped to {@link java.time.LocalDateTime}.
     * <p>
     * Wire format: {@code {"type": "string", "format": "local-date-time"}} (custom;
     * RFC 3339 {@code date-time} mandates an offset). Validators that don't recognise
     * the format fall back to plain {@code type: string}.
     * <p>
     * Canonical long encoding: epoch-milliseconds interpreted in UTC (documented
     * convention — IEC 61131-3 gives no zone, so UTC is the default for long→local
     * conversion).
     * <p>
     * Primary producer: PLC4X {@code DATE_AND_TIME}, {@code LDATE_AND_TIME},
     * {@code DATE_AND_LTIME}.
     */
    LOCAL_DATE_TIME,

    /**
     * A time-based amount — mapped to {@link java.time.Duration}.
     * <p>
     * Wire format: {@code {"type": "string", "format": "duration"}} (ISO 8601 duration,
     * e.g. {@code PT1H30M45S}). Signed durations are supported
     * (e.g. {@code -PT5S}).
     * <p>
     * Canonical long encoding: nanoseconds
     * (see {@link java.time.Duration#ofNanos(long)}).
     * <p>
     * Primary producer: PLC4X {@code TIME}, {@code LTIME}.
     */
    DURATION
}
