package fr.thefoxy41.dataManager.mysql;

import fr.thefoxy41.dataManager.auth.Credentials;

public class MysqlCredentials extends Credentials {
    private String databaseName;
    private int poolSize;

    MysqlCredentials() {}

    MysqlCredentials(String host, String user, String pass, String databaseName, String serviceName, int port, int poolSize) {
        super(host, user, pass, serviceName, port);
        this.databaseName = databaseName;
        this.poolSize =  poolSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public String getDatabase() {
        return databaseName;
    }

    public String toUri() {
        return "jdbc:mysql://" +
                host +
                ":" +
                port +
                "/" +
                databaseName
                + "?useSSL=false";
    }
}
