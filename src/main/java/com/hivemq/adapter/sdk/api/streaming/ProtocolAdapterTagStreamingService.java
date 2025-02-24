package com.hivemq.adapter.sdk.api.streaming;

import com.hivemq.adapter.sdk.api.data.DataPoint;

import java.util.List;

public interface ProtocolAdapterTagStreamingService {
    void feed(String tag, List<DataPoint> dataPoints);
}
