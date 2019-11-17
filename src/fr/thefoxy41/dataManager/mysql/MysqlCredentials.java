package fr.thefoxy41.dataManager.mysql;

import fr.thefoxy41.dataManager.auth.Credentials;

public class MysqlCredentials extends Credentials {
    private String dbName;
    private int poolSize;

    MysqlCredentials() {}

    MysqlCredentials(String host, String user, String pass, String dbName, String serviceName, int port, int poolSize) {
        super(host, user, pass, serviceName, port);
        this.dbName = dbName;
        this.poolSize =  poolSize;
    }

    public int getPoolSize() {
        return Math.max(poolSize, 1);
    }

    public String getDatabase() {
        return dbName;
    }

    public String toUri() {
        return "jdbc:mysql://" +
                host +
                ":" +
                port +
                "/" +
                dbName
                + "?useSSL=false";
    }
}
