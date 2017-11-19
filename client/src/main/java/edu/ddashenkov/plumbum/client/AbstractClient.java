package edu.ddashenkov.plumbum.client;

import com.google.protobuf.Message;
import io.grpc.Channel;
import io.spine.client.ActorRequestFactory;
import io.spine.client.Query;
import io.spine.client.QueryResponse;
import io.spine.client.grpc.CommandServiceGrpc;
import io.spine.client.grpc.CommandServiceGrpc.CommandServiceBlockingStub;
import io.spine.client.grpc.QueryServiceGrpc;
import io.spine.client.grpc.QueryServiceGrpc.QueryServiceBlockingStub;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.core.UserId;
import io.spine.protobuf.AnyPacker;

import static io.spine.protobuf.Messages.newInstance;
import static java.util.Collections.singleton;

abstract class AbstractClient {

    private final CommandServiceBlockingStub commandService;
    private final QueryServiceBlockingStub queryService;

    private final ActorRequestFactory requestFactory;

    AbstractClient(Channel channel, UserId currentUser) {
        this.commandService = CommandServiceGrpc.newBlockingStub(channel);
        this.queryService = QueryServiceGrpc.newBlockingStub(channel);
        this.requestFactory = ActorRequestFactory.newBuilder()
                                                 .setActor(currentUser)
                                                 .build();
    }

    Ack sendCommand(Message commandMsg) {
        final Command command = requestFactory.command()
                                              .create(commandMsg);
        return commandService.post(command);
    }

    <M extends Message> M read(Class<M> type, Message id) {
        final Query query = requestFactory.query()
                                          .byIds(type, singleton(id));
        return read(query, newInstance(type));
    }

    <M extends Message> M read(Query query, M defaultInstance) {
        final QueryResponse response = queryService.read(query);
        final M result = response.getMessagesList()
                                 .stream()
                                 .map(AnyPacker::<M>unpack)
                                 .findAny()
                                 .orElse(defaultInstance);
        return result;
    }

    ActorRequestFactory requestFactory() {
        return requestFactory;
    }
}
