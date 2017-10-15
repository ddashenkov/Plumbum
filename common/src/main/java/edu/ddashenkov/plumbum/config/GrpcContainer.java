package edu.ddashenkov.plumbum.config;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.spine.client.ConnectionConstants;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Wrapping container for gRPC server.
 *
 * <p>Maintains and deploys several of gRPC services within a single server.
 *
 * <p>Uses {@link ServerServiceDefinition}s of each service.
 */
public class GrpcContainer {

    private static final String SERVER_NOT_STARTED_MSG =
            "gRPC server was not started or is shut down already.";

    private final int port;
    private final ImmutableSet<ServerServiceDefinition> services;

    @Nullable
    private io.grpc.Server grpcServer;

    public static Builder newBuilder() {
        return new Builder();
    }

    private GrpcContainer(Builder builder) {
        this.port = builder.getPort();
        this.services = builder.getServices();
    }

    /**
     * Starts the service.
     *
     * @throws IOException if unable to bind
     */
    public void start() throws IOException {
        checkState(grpcServer == null, "gRPC server is started already.");
        grpcServer = createGrpcServer();
        grpcServer.start();
    }

    /**
     * Initiates an orderly shutdown in which existing calls continue but new calls are rejected.
     */
    public void shutdown() {
        checkState(grpcServer != null, SERVER_NOT_STARTED_MSG);
        grpcServer.shutdown();
        grpcServer = null;
    }

    /** Waits for the service to become terminated. */
    public void awaitTermination() {
        checkState(grpcServer != null, SERVER_NOT_STARTED_MSG);
        try {
            grpcServer.awaitTermination();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private Server createGrpcServer() {
        final ServerBuilder builder = ServerBuilder.forPort(port)
                                                   .useTransportSecurity(null, null);
        for (ServerServiceDefinition service : services) {
            builder.addService(service);
        }

        return builder.build();
    }

    public static class Builder {

        private int port = ConnectionConstants.DEFAULT_CLIENT_SERVICE_PORT;
        private final Set<ServerServiceDefinition> serviceDefinitions = Sets.newHashSet();

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public int getPort() {
            return this.port;
        }

        public Builder addService(BindableService service) {
            serviceDefinitions.add(service.bindService());
            return this;
        }

        public Builder removeService(ServerServiceDefinition service) {
            serviceDefinitions.remove(service);
            return this;
        }

        public ImmutableSet<ServerServiceDefinition> getServices() {
            return ImmutableSet.copyOf(serviceDefinitions);
        }

        public GrpcContainer build() {
            return new GrpcContainer(this);
        }
    }
}
