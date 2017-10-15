package edu.ddashenkov.plumbum.record;

import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;

/**
 * @author Dmytro Dashenkov
 */
@SuppressWarnings("unused") // Reflective access.
public class RecordAggregate extends Aggregate<RecordId, Record, RecordVBuilder> {

    protected RecordAggregate(RecordId id) {
        super(id);
    }

    @Assign
    RecordCreated handle(CreateRecord command) {
        final RecordCreated event = RecordCreated.newBuilder()
                                                 .setId(command.getId())
                                                 .setUserId(command.getUserId())
                                                 .setDisplayName(command.getDisplayName())
                                                 .build();
        return event;
    }

    @Assign
    TextAppended handle(AppendText command) {
        final TextAppended event = TextAppended.newBuilder()
                                               .setId(command.getId())
                                               .addAllNewPoints(command.getNewPointsList())
                                               .build();
        return event;
    }

    @Assign
    RecordNameChanged handle(SetRecordName command) {
        final RecordNameChanged event = RecordNameChanged.newBuilder()
                                                         .setId(command.getId())
                                                         .setNewName(command.getNewName())
                                                         .build();
        return event;
    }

    @Apply
    private void on(RecordCreated event) {
        getBuilder().setId(event.getId())
                    .setUserId(event.getUserId())
                    .setDisplayName(event.getDisplayName());
    }

    @Apply
    private void on(TextAppended event) {
        getBuilder().addAllPoints(event.getNewPointsList());
    }

    @Apply
    private void on(RecordNameChanged event) {
        getBuilder().setDisplayName(event.getNewName());
    }
}
