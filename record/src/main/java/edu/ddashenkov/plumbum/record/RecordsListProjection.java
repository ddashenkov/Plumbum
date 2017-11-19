package edu.ddashenkov.plumbum.record;

import com.google.protobuf.Empty;
import edu.ddashenkov.plumbum.user.UserCreated;
import io.spine.core.React;
import io.spine.core.UserId;
import io.spine.server.aggregate.Apply;
import io.spine.server.entity.storage.Column;
import io.spine.server.projection.Projection;

import java.util.List;

/**
 * @author Dmytro Dashenkov
 */
@SuppressWarnings("unused") // Reflective access.
public class RecordsListProjection extends Projection<UserId, RecordList, RecordListVBuilder> {

    protected RecordsListProjection(UserId id) {
        super(id);
    }

    @Column
    public UserId getOwner() {
        return getState().getUserId();
    }

    @React(external = true)
    private Empty on(UserCreated event) {
        getBuilder().setUserId(event.getUserId());
        return Empty.getDefaultInstance();
    }

    @Apply
    private void on(RecordCreated event) {
        final Record record = Record.newBuilder()
                                    .setId(event.getId())
                                    .setDisplayName(event.getDisplayName())
                                    .build();
        getBuilder().addRecords(record);
    }

    @Apply
    private void on(RecordNameChanged event) {
        int index = 0;
        final List<Record> list = getState().getRecordsList();
        for (Record record : list) {
            if (record.getId().equals(event.getId())) {
                break;
            }
            index++;
        }
        if (index == list.size()) {
            throw new IllegalStateException();
        }
        final Record newRecord = list.get(index)
                                     .toBuilder()
                                     .setDisplayName(event.getNewName())
                                     .build();
        getBuilder().setRecords(index, newRecord);
    }
}
