package fr.thefoxy41.dataManager.mysql;

import fr.thefoxy41.dataManager.auth.Credentials;

public class MysqlCredentials extends Credentials {
    private String dataBaseName;
    private int poolSize;

    MysqlCredentials(String host, String user, String pass, String dataBaseName, String serviceName, int port, int poolSize) {
        super(host, user, pass, serviceName, port);
        this.dataBaseName = dataBaseName;
        this.poolSize =  poolSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public String toUri() {
        return "jdbc:mysql://" +
                host +
                ":" +
                port +
                "/" +
                dataBaseName
                + "?useSSL=false";
    }
}
