package com.hivemq.adapter.sdk.api.datapoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Function;

public interface DataPointBuilder<R> {
    
    @NotNull DataPointBuilder<R> setValue(final @NotNull String value);

    @NotNull DataPointBuilder<R> setValue(final int value);

    @NotNull DataPointBuilder<R> setValue(final long value);

    @NotNull DataPointBuilder<R> setValue(final double value);

    @NotNull DataPointBuilder<R> setValue(final float value);

    @NotNull DataPointBuilder<R> setValue(final boolean value);

    @NotNull DataPointBuilder<R> setValue(final short value);

    @NotNull DataPointBuilder<R> setValue(final @NotNull BigDecimal value);

    @NotNull DataPointBuilder<R> setValue(final @NotNull BigInteger value);

    @NotNull DataPointBuilder<R> setValue(final byte @NotNull [] value);

    @NotNull DataPointBuilder<R> setNullValue();

    @NotNull ObjectBuilder<DataPointBuilder<R>> valueStart();

    @NotNull ObjectBuilder<DataPointBuilder<R>> metadataStart();

    @NotNull ObjectBuilder<DataPointBuilder<R>> protocolTagMetadataStart();

    @NotNull ObjectBuilder<DataPointBuilder<R>> protocolDeviceMetadataStart();

    @NotNull ObjectBuilder<DataPointBuilder<R>> adapterDatapointMetadataStart();

    @NotNull ObjectBuilder<DataPointBuilder<R>> adapterTagMetadataStart();

    @NotNull ObjectBuilder<DataPointBuilder<R>> adapterDeviceMetadataStart();

    R finish();

    interface ObjectBuilder<P> {

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final @NotNull String value);

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final int value);

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final long value);

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final double value);

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final float value);

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final boolean value);

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final @NotNull BigDecimal value);

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final @NotNull BigInteger value);

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final byte @NotNull [] value);

        @NotNull ObjectBuilder<P> add(final @NotNull String key, final short value);

        @NotNull ObjectBuilder<P> addNull(final @NotNull String key);

        @NotNull ObjectBuilder<ObjectBuilder<P>> objectStart(final @NotNull String key);

        @NotNull ArrayBuilder<ObjectBuilder<P>> arrayStart(final @NotNull String key);

        @NotNull P valueStop();

        @NotNull P metadataStop();

        @NotNull P protocolTagMetadataStop();

        @NotNull P protocolDeviceMetadataStop();

        @NotNull P adapterDatapointMetadataStop();

        @NotNull P adapterTagMetadataStop();

        @NotNull P adapterDeviceMetadataStop();

        @NotNull P objectEnd();
    }

    interface ArrayBuilder<P> {

        @NotNull ArrayBuilder<P> add(final @NotNull String value);

        @NotNull ArrayBuilder<P> add(final int value);

        @NotNull ArrayBuilder<P> add(final long value);

        @NotNull ArrayBuilder<P> add(final double value);

        @NotNull ArrayBuilder<P> add(final float value);

        @NotNull ArrayBuilder<P> add(final boolean value);

        @NotNull ArrayBuilder<P> add(final @NotNull BigDecimal value);

        @NotNull ArrayBuilder<P> add(final @NotNull BigInteger value);

        @NotNull ArrayBuilder<P> add(final byte @NotNull [] value);

        @NotNull ArrayBuilder<P> add(final short value);

        @NotNull ArrayBuilder<P> addNull();

        @NotNull ObjectBuilder<ArrayBuilder<P>> objectStart();

        @NotNull P arrayEnd();
    }
}
