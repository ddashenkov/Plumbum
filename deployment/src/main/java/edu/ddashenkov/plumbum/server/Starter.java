package edu.ddashenkov.plumbum.server;

import edu.ddashenkov.plumbum.config.Server;
import edu.ddashenkov.plumbum.record.RecordRepository;
import edu.ddashenkov.plumbum.record.RecordsListRepository;
import edu.ddashenkov.plumbum.user.UserRepository;
import io.spine.server.BoundedContext;
import io.spine.server.entity.Repository;

import static edu.ddashenkov.plumbum.config.BoundedContexts.newBoundedContext;

public final class Starter {

    private static final int PORT = 50051;

    private static final String USERS = "Users";
    private static final String RECORDS = "Records";

    private Starter() {
        // Prevent utility class instantiation.
    }

    public static void main(String[] args) {
        final BoundedContext userBc = createUsersBoundedContext();
        final BoundedContext recordsRc = createRecordsBoundedContext();
        final Server server = new Server(PORT, userBc, recordsRc);
        server.start();
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
}
