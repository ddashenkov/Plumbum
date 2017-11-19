package edu.ddashenkov.plumbum.client;

import edu.ddashenkov.plumbum.record.AppendText;
import edu.ddashenkov.plumbum.record.CreateRecord;
import edu.ddashenkov.plumbum.record.Record;
import edu.ddashenkov.plumbum.record.RecordId;
import edu.ddashenkov.plumbum.record.RecordList;
import edu.ddashenkov.plumbum.record.SetRecordName;
import edu.ddashenkov.plumbum.user.ChangeUserName;
import edu.ddashenkov.plumbum.user.User;
import io.grpc.Channel;
import io.spine.client.Query;
import io.spine.core.Ack;
import io.spine.core.UserId;

import static io.spine.client.ColumnFilters.eq;

public final class PlumbumClient extends AbstractClient {

    private PlumbumClient(Channel channel, UserId currentUser) {
        super(channel, currentUser);
    }

    public static PlumbumClient instance(Channel channel, UserId user) {
        return new PlumbumClient(channel, user);
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
        final Query query = requestFactory().query()
                                            .select(RecordList.class)
                                            .where(eq("owner", requestFactory().getActor()))
                                            .build();
        return read(query);
    }
}
