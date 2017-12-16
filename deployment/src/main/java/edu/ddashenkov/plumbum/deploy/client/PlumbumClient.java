package edu.ddashenkov.plumbum.deploy.client;

import edu.ddashenkov.plumbum.record.AppendText;
import edu.ddashenkov.plumbum.record.CreateRecord;
import edu.ddashenkov.plumbum.record.Record;
import edu.ddashenkov.plumbum.record.RecordId;
import edu.ddashenkov.plumbum.record.RecordList;
import edu.ddashenkov.plumbum.record.SetRecordName;
import edu.ddashenkov.plumbum.user.ChangeUserName;
import edu.ddashenkov.plumbum.user.User;
import io.spine.client.Query;
import io.spine.core.Ack;
import io.spine.core.UserId;

import java.util.Set;

import static java.util.Collections.singleton;

public final class PlumbumClient extends AbstractClient {

    private PlumbumClient(UserId currentUser) {
        super(currentUser);
    }

    public static PlumbumClient instance(UserId user) {
        return new PlumbumClient(user);
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
        final Set<UserId> ids = singleton(requestFactory().getActor());
        final Query query = requestFactory().query()
                                            .byIds(RecordList.class, ids);
        return read(query, RecordList.getDefaultInstance());
    }
}
