package edu.ddashenkov.plumbum.config;

import io.spine.server.BoundedContext;
import io.spine.server.CommandService;
import io.spine.server.QueryService;
import io.spine.server.SubscriptionService;
import io.spine.server.transport.GrpcContainer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;

/**
 * @author Dmytro Dashenkov
 */
public final class Server {

    private final int port;
    private final List<BoundedContext> boundedContexts;
    @Nullable
    private GrpcContainer container;

    public Server(int port, BoundedContext... boundedContexts) {
        this.port = port;
        this.boundedContexts = copyOf(boundedContexts);
    }

    public void start() {
        try {
            container = prepareContainer();
            container.start();
            container.awaitTermination();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void shutDown() {
        checkNotNull(container);
        container.shutdown();
    }

    private GrpcContainer prepareContainer() {
        final GrpcContainer.Builder container = GrpcContainer.newBuilder()
                                                             .setPort(port);
        final QueryService.Builder queryService = QueryService.newBuilder();
        final CommandService.Builder commandService = CommandService.newBuilder();
        final SubscriptionService.Builder subscriptionService = SubscriptionService.newBuilder();
        for (BoundedContext context : boundedContexts) {
            queryService.add(context);
            commandService.add(context);
            subscriptionService.add(context);

            container.addService(context);
        }
        container.addService(queryService.build());
        container.addService(commandService.build());
        return container.build();
    }
}
