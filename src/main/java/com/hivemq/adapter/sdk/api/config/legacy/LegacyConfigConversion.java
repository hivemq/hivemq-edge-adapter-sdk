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
