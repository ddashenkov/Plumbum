package edu.ddashenkov.plumbum.deploy.config;

import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.jdbc.JdbcStorageFactory;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

/**
 * @author Dmytro Dashenkov
 */
public final class HerokuPostgresConfig {

    public static StorageFactory storageFactory() {
        final DataSource dataSource = herokuDataSource();
        final StorageFactory factory = JdbcStorageFactory.newBuilder()
                                                         .setDataSource(dataSource)
                                                         .build();
        return factory;
    }

    private static DataSource herokuDataSource() {
        final String url = System.getenv("DATABASE_URL");
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        return dataSource;
    }
}
