package edu.ddashenkov.plumbum.client;

import com.google.protobuf.Message;
import edu.ddashenkov.plumbum.record.AppendText;
import edu.ddashenkov.plumbum.record.CreateRecord;
import edu.ddashenkov.plumbum.record.Record;
import edu.ddashenkov.plumbum.record.RecordId;
import edu.ddashenkov.plumbum.record.RecordList;
import edu.ddashenkov.plumbum.record.SetRecordName;
import edu.ddashenkov.plumbum.user.ChangeUserName;
import edu.ddashenkov.plumbum.user.CreateUser;
import edu.ddashenkov.plumbum.user.User;
import io.grpc.ManagedChannel;
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

import static io.grpc.ManagedChannelBuilder.forAddress;
import static io.spine.client.ColumnFilters.eq;
import static java.util.Collections.singleton;

public final class PlumbumClient implements AutoCloseable {

    private static final UserId ANONYMOUS = UserId.newBuilder()
                                                  .setValue("-")
                                                  .build();
    private final ManagedChannel channel;

    private final CommandServiceBlockingStub commandService;
    private final QueryServiceBlockingStub queryService;

    private final ActorRequestFactory requestFactory;

    private PlumbumClient(String host, int port, UserId currentUser) {
        this.channel = forAddress(host, port).build();
        this.commandService = CommandServiceGrpc.newBlockingStub(channel);
        this.queryService = QueryServiceGrpc.newBlockingStub(channel);
        this.requestFactory = ActorRequestFactory.newBuilder()
                                                 .setActor(currentUser)
                                                 .build();
    }

    public static PlumbumClient anonymous(String host, int post) {
        return new PlumbumClient(host, post, ANONYMOUS);
    }

    public static PlumbumClient forUser(UserId user, String host, int port) {
        return new PlumbumClient(host, port, user);
    }

    public Ack createUser(CreateUser command) {
        return sendCommand(command);
    }

    public Ack renameUser(ChangeUserName command) {
        return sendCommand(command);
    }

    public User getUser(UserId id) {
        return read(User.class, id);
    }

    public Ack createRecerd(CreateRecord command) {
        return sendCommand(command);
    }

    public Ack appendText(AppendText command) {
        return sendCommand(command);
    }

    public Ack renameRecord(SetRecordName command) {
        return sendCommand(command);
    }

    public Record getRecord(RecordId id) {
        return read(Record.class, id);
    }

    public RecordList getMyRecords() {
        final Query query = requestFactory.query()
                                          .select(RecordList.class)
                                          .where(eq("owner", requestFactory.getActor()))
                                          .build();
        return read(query);
    }

    private Ack sendCommand(Message commandMsg) {
        final Command command = requestFactory.command()
                                              .create(commandMsg);
        return commandService.post(command);
    }

    private <M extends Message> M read(Class<M> type, Message id) {
        final Query query = requestFactory.query()
                                          .byIds(type, singleton(id));
        return read(query);
    }

    private <M extends Message> M read(Query query) {
        final QueryResponse response = queryService.read(query);
        final M result = response.getMessagesList()
                                 .stream()
                                 .map(AnyPacker::<M>unpack)
                                 .findAny()
                                 .orElseThrow(() -> new IllegalArgumentException(query.toString()));
        return result;
    }

    @Override
    public void close() throws Exception {
        channel.shutdown();
    }
}
