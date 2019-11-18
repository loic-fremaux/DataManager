package fr.thefoxy41.dataManager.redis;

import fr.thefoxy41.dataManager.auth.Credentials;

public class RedisCredentials extends Credentials {
    private int databaseId;

    public RedisCredentials() {}

    public RedisCredentials(String host, String password, int port, int databaseId, String clientName) {
        super(host, "", password, clientName, port);
        this.databaseId = databaseId;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public String toUri() {
        return host + ":" + port;
    }
}
