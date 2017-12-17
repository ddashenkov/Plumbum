package edu.ddashenkov.plumbum.record;

import edu.ddashenkov.plumbum.user.UserCreated;
import io.spine.core.Subscribe;
import io.spine.core.UserId;
import io.spine.server.projection.Projection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Dmytro Dashenkov
 */
@SuppressWarnings("unused") // Reflective access.
public class RecordsListProjection extends Projection<UserId, RecordList, RecordListVBuilder> {

    protected RecordsListProjection(UserId id) {
        super(id);
    }

    @Subscribe(external = true)
    public void on(UserCreated event) {
        getBuilder().setUserId(event.getUserId());
    }

    @Subscribe
    public void on(RecordCreated event) {
        log().info("RecordCreated {}", event);
        final Record record = Record.newBuilder()
                                    .setId(event.getId())
                                    .setUserId(event.getUserId())
                                    .setDisplayName(event.getDisplayName())
                                    .build();
        getBuilder().setUserId(event.getUserId())
                    .addRecords(record);
    }

    @Subscribe
    public void on(RecordNameChanged event) {
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

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }

    private enum LogSingleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(RecordsListProjection.class);
    }
}
