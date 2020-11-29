package fr.lfremaux.dataManager.mysql;

import fr.lfremaux.dataManager.auth.Credentials;

public class MysqlCredentials extends Credentials {
    private String dbName;

    MysqlCredentials() {
    }

    MysqlCredentials(String host, String user, String pass, String dbName, String serviceName, int port, int poolSize) {
        super(host, user, pass, serviceName, port);
        this.dbName = dbName;
        this.poolSize = poolSize;
    }

    public String getDbName() {
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
