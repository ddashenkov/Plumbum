package edu.ddashenkov.plumbum.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public final class Channels {

    private static final Channel LOCALHOST_CHANNEL = ManagedChannelBuilder.forAddress("localhost", 50051)
                                                                          .usePlaintext(true)
                                                                          .build();
    private Channels() {
        // Prevent utility class instantiation.
    }

    public static Channel getDefault() {
        return localhost();
    }

    public static Channel forHostPort(String host, int port) {
        return ManagedChannelBuilder.forAddress(host, port)
                                    .build();
    }

    private static Channel localhost() {
        return LOCALHOST_CHANNEL;
    }
}
