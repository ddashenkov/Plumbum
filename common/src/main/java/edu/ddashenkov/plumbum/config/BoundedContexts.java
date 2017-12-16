package edu.ddashenkov.plumbum.config;

import io.spine.core.BoundedContextName;
import io.spine.server.BoundedContext;
import io.spine.server.entity.Repository;
import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.memory.InMemoryStorageFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Dmytro Dashenkov
 */
public final class BoundedContexts {

    private BoundedContexts() {
        // Prevent utility class instantiation.
    }

    public static BoundedContext newBoundedContext(String name, Repository<?, ?>... repositories) {
        checkNotNull(name);
        checkNotNull(repositories);
        final BoundedContextName contextName = BoundedContext.newName(name);
        final StorageFactory storageFactory = InMemoryStorageFactory.newInstance(contextName, false);
        final BoundedContext bc = BoundedContext.newBuilder()
                                                .setName(name)
                                                .setStorageFactorySupplier(() -> storageFactory)
                                                .build();
        for (Repository<?, ?> repo : repositories) {
            bc.register(repo);
        }
        return bc;
    }
}
