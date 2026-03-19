package com.hivemq.adapter.sdk.api.datapoint;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

public interface DataPointBuilder<R> {

    @NotNull DataPointBuilder<R> value(boolean value);

    @NotNull DataPointBuilder<R> value(byte value);

    @NotNull DataPointBuilder<R> value(short value);

    @NotNull DataPointBuilder<R> value(int value);

    @NotNull DataPointBuilder<R> value(long value);

    @NotNull DataPointBuilder<R> value(float value);

    @NotNull DataPointBuilder<R> value(double value);

    @NotNull DataPointBuilder<R> value(@NotNull String value);

    @NotNull DataPointBuilder<R> value(byte @NotNull [] value);

    @NotNull DataPointBuilder<R> value(@NotNull BigDecimal value);

    @NotNull DataPointBuilder<R> value(@NotNull BigInteger value);

    @NotNull DataPointBuilder<R> value(@NotNull JsonNode value);

    @NotNull DataPointBuilder<R> valueNull();

    @NotNull ObjectBuilder<DataPointBuilder<R>> startObjectValue();

    @NotNull ArrayBuilder<DataPointBuilder<R>> startArrayValue();

    @NotNull ObjectBuilder<DataPointBuilder<R>> startObjectMetadata();

    @NotNull ObjectBuilder<DataPointBuilder<R>> startObjectContext();

    @NotNull DataPointBuilder<R> timestamp(long epochMillis);

    @NotNull DataPointBuilder<R> timestamp(@NotNull Instant instant);

    R endDataPoint();

    interface ObjectBuilder<P> {

        @NotNull ObjectBuilder<P> put(@NotNull String key, boolean value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, byte value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, short value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, int value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, long value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, float value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, double value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, @NotNull String value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, byte @NotNull [] value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, @NotNull BigDecimal value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, @NotNull BigInteger value);

        @NotNull ObjectBuilder<P> put(@NotNull String key, @NotNull JsonNode value);

        @NotNull ObjectBuilder<P> putNull(@NotNull String key);

        @NotNull ObjectBuilder<ObjectBuilder<P>> startObject(@NotNull String key);

        @NotNull ArrayBuilder<ObjectBuilder<P>> startArray(@NotNull String key);

        @NotNull P endObject();
    }

    interface ArrayBuilder<P> {

        @NotNull ArrayBuilder<P> add(boolean value);

        @NotNull ArrayBuilder<P> add(byte value);

        @NotNull ArrayBuilder<P> add(short value);

        @NotNull ArrayBuilder<P> add(int value);

        @NotNull ArrayBuilder<P> add(long value);

        @NotNull ArrayBuilder<P> add(float value);

        @NotNull ArrayBuilder<P> add(double value);

        @NotNull ArrayBuilder<P> add(@NotNull String value);

        @NotNull ArrayBuilder<P> add(byte @NotNull [] value);

        @NotNull ArrayBuilder<P> add(@NotNull BigDecimal value);

        @NotNull ArrayBuilder<P> add(@NotNull BigInteger value);

        @NotNull ArrayBuilder<P> add(@NotNull JsonNode value);

        @NotNull ArrayBuilder<P> addNull();

        @NotNull ObjectBuilder<ArrayBuilder<P>> startObject();

        @NotNull ArrayBuilder<ArrayBuilder<P>> startArray();

        @NotNull P endArray();
    }
}
