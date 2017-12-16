package edu.ddashenkov.plumbum.webadapter;

import com.google.protobuf.Message;
import io.spine.client.ActorRequestFactory;
import io.spine.client.Query;
import io.spine.client.QueryResponse;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.core.UserId;
import io.spine.protobuf.AnyPacker;
import io.spine.server.CommandService;
import io.spine.server.QueryService;

import java.util.Collection;

import static io.spine.protobuf.Messages.newInstance;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

abstract class AbstractClient {

    private final CommandService commandService;
    private final QueryService queryService;

    private final ActorRequestFactory requestFactory;

    AbstractClient(UserId currentUser) {
        this.requestFactory = ActorRequestFactory.newBuilder()
                                                 .setActor(currentUser)
                                                 .build();
        this.commandService = Backend.instance().getCommandService();
        this.queryService = Backend.instance().getQueryService();
    }

    Ack sendCommand(Message commandMsg) {
        final Command command = requestFactory.command()
                                              .create(commandMsg);
        final BlockingObserver<Ack> observer = BlockingObserver.instance();
        commandService.post(command, observer);
        return observer.getSingleValue();
    }

    <M extends Message> M read(Class<M> type, Message id) {
        final Query query = requestFactory.query()
                                          .byIds(type, singleton(id));
        return read(query, newInstance(type));
    }

    <M extends Message> M read(Query query, M defaultInstance) {
        return this.<M>read(query).stream()
                                  .findAny()
                                  .orElse(defaultInstance);
    }

    <M extends Message> Collection<M> read(Query query) {
        final BlockingObserver<QueryResponse> observer = BlockingObserver.instance();
        queryService.read(query, observer);
        final QueryResponse response = observer.getSingleValue();

        final Collection<M> result = response.getMessagesList()
                                             .stream()
                                             .map(AnyPacker::<M>unpack)
                                             .collect(toList());
        return result;
    }

    ActorRequestFactory requestFactory() {
        return requestFactory;
    }
}
