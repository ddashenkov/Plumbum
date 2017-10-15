package edu.ddashenkov.plumbum.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.spine.server.storage.StorageFactory;
import io.spine.server.storage.jdbc.JdbcStorageFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Dmytro Dashenkov
 */
final class MySqlConfig {

    private static MysqlDataSource dataSource;

    static {
        final Properties dbConfig = new Properties();
        try (InputStream in = MySqlConfig.class.getClassLoader().getResourceAsStream("config/db.properties")) {
            checkNotNull(in, "Could not read configuration file `config/db.properties` from classpath.");
            dbConfig.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        dataSource = new MysqlDataSource();
        dataSource.setDatabaseName(Prop.DB_NAME.get(dbConfig));
        dataSource.setUrl(Prop.URL.get(dbConfig));
        dataSource.setUser(Prop.USER.get(dbConfig));
        dataSource.setPassword(Prop.PASSWORD.get(dbConfig));
    }

    private MySqlConfig() {
        // Prevent utility class instantiation.
    }

    static StorageFactory storageFactory() {
        final StorageFactory factory = JdbcStorageFactory.newBuilder()
                                                         .setDataSource(dataSource)
                                                         .build();
        return factory;
    }

    private enum Prop {

        DB_NAME("db.name"),
        URL("db.url"),
        USER("user.name"),
        PASSWORD("user.password");

        private String name;

        Prop(String name) {
            this.name = name;
        }

        private String get(Properties props) {
            return props.getProperty(name);
        }
    }
}
