package edu.ddashenkov.plumbum.config;

import io.spine.server.BoundedContext;
import io.spine.server.entity.Repository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Dmytro Dashenkov
 */
public final class BoundedContexts {

    private BoundedContexts() {
        // Prevent utility class instantiation.
    }

    public static BoundedContext newBoundeContext(String name, Repository<?, ?>... repositories) {
        checkNotNull(name);
        checkNotNull(repositories);
        final BoundedContext bc = BoundedContext.newBuilder()
                                                .setName(name)
                                                .setStorageFactorySupplier(MySqlConfig::storageFactory)
                                                .build();
        for (Repository<?, ?> repo : repositories) {
            bc.register(repo);
        }
        return bc;
    }
}
