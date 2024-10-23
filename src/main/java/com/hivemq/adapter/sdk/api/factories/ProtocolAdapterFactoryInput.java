package com.hivemq.adapter.sdk.api.factories;

import com.hivemq.adapter.sdk.api.events.EventService;
import com.hivemq.adapter.sdk.api.services.ProtocolAdapterTagService;
import org.jetbrains.annotations.NotNull;

/**
 * This interface offers access to classes that might be needed during instantiation of a ProtocolAdapterFactory
 */
public interface ProtocolAdapterFactoryInput {

    /**
     * @return True: Edge has writing enabled, false: Edge does not support writing.
     */
    boolean isWritingEnabled();

    /**
     * @return {@link ProtocolAdapterTagService} to add and resolve {@link com.hivemq.adapter.sdk.api.tag.Tag}.
     */
    @NotNull
    ProtocolAdapterTagService protocolAdapterTagService();

    /**
     * @return {@link EventService} to create and fire events that are displayed at the UI.
     */
    @NotNull
    EventService eventService();

}
