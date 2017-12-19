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
        final String url = "postgres://wrueucyquwohzk:6a13ae2fd05cb8e4e263a4c21e3d05c3c78ee45fca3aaa69b2ca0808e1f43ca8" +
                "@ec2-54-235-244-185.compute-1.amazonaws.com:5432/dd749jf1trjnqu";
        //System.getenv("DATABASE_URL");
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        return dataSource;
    }
}
