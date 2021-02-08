package fr.lfremaux.dataManager.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MysqlAccess {
    private final MysqlCredentials credentials;
    private HikariDataSource hikariDataSource;

    MysqlAccess(MysqlCredentials credentials) {
        this.credentials = credentials;
        setUpHikariCP();
    }

    MysqlAccess(MysqlCredentials credentials, MysqlCustomConfig config) {
        this.credentials = credentials;
        setUpHikariCP(config);
    }

    private void setUpHikariCP(MysqlCustomConfig hikariConfig) {
        hikariConfig.setJdbcUrl(credentials.toUri());
        hikariConfig.setUsername(credentials.getUser());
        hikariConfig.setPassword(credentials.getPassword());

        hikariConfig.setMaximumPoolSize(credentials.getPoolSize());
        hikariConfig.setPoolName(credentials.getClientName());
        hikariConfig.setMinimumIdle(credentials.getMinimumIdle());

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    private void setUpHikariCP() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(credentials.toUri());
        hikariConfig.setUsername(credentials.getUser());
        hikariConfig.setPassword(credentials.getPassword());

        hikariConfig.setMaximumPoolSize(credentials.getPoolSize());
        hikariConfig.setMaxLifetime(30_000L);
        hikariConfig.setIdleTimeout(30_000L);
        hikariConfig.setLeakDetectionThreshold(10_000L);
        hikariConfig.setConnectionTimeout(10_000L);
        hikariConfig.setPoolName(credentials.getClientName());
        hikariConfig.setMinimumIdle(credentials.getMinimumIdle());

        hikariConfig.addDataSourceProperty("autoReconnect", true);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public void closePool() {
        this.hikariDataSource.close();
    }

    public MysqlCredentials getCredentials() {
        return credentials;
    }

    public Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }
}
