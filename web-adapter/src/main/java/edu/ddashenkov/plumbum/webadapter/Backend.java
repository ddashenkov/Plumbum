package edu.ddashenkov.plumbum.webadapter;

import edu.ddashenkov.plumbum.record.RecordRepository;
import edu.ddashenkov.plumbum.record.RecordsListRepository;
import edu.ddashenkov.plumbum.user.UserRepository;
import io.spine.server.BoundedContext;
import io.spine.server.CommandService;
import io.spine.server.QueryService;
import io.spine.server.entity.Repository;

import static edu.ddashenkov.plumbum.webadapter.BoundedContexts.newBoundedContext;

final class Backend {

    private static final String USERS = "Users";
    private static final String RECORDS = "Records";

    private final QueryService queryService;
    private final CommandService commandService;

    private Backend(BoundedContext... boundedContexts) {
        this.commandService = createCommandService(boundedContexts);
        this.queryService = createQueryService(boundedContexts);
    }

    private CommandService createCommandService(BoundedContext... boundedContexts) {
        final CommandService.Builder builder = CommandService.newBuilder();
        for (BoundedContext boundedContext : boundedContexts) {
            builder.add(boundedContext)
                   .add(boundedContext);
        }
        return builder.build();
    }

    private QueryService createQueryService(BoundedContext... boundedContexts) {
        final QueryService.Builder builder = QueryService.newBuilder();
        for (BoundedContext boundedContext : boundedContexts) {
            builder.add(boundedContext)
                   .add(boundedContext);
        }
        return builder.build();
    }

    private static BoundedContext createUsersBoundedContext() {
        final Repository<?, ?> userRepository = new UserRepository();
        final BoundedContext userBc = newBoundedContext(USERS, userRepository);
        return userBc;
    }

    private static BoundedContext createRecordsBoundedContext() {
        final Repository<?, ?> recordRepository = new RecordRepository();
        final Repository<?, ?> recordsListRepository = new RecordsListRepository();
        final BoundedContext recordsRc = newBoundedContext(RECORDS, recordRepository, recordsListRepository);
        return recordsRc;
    }

    QueryService getQueryService() {
        return queryService;
    }

    CommandService getCommandService() {
        return commandService;
    }

    static Backend instance() {
        return Singleton.INSTANCE.value;
    }

    private enum Singleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Backend value = new Backend(createRecordsBoundedContext(),
                                                  createUsersBoundedContext());
    }
}
