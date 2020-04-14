package fr.thefoxy41.dataManager.redis;

import fr.thefoxy41.dataManager.auth.Credentials;

public class RedisCredentials extends Credentials {
    private int databaseId;
    private int minimumSubscriptionIdle;
    protected int subscriptionPoolSize;

    public RedisCredentials() {
    }

    public RedisCredentials(String host, String password, int port, int databaseId, String clientName) {
        super(host, "", password, clientName, port);
        this.databaseId = databaseId;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public int getMinimumSubscriptionIdle() {
        return minimumSubscriptionIdle;
    }

    public int getSubscriptionPoolSize() {
        return subscriptionPoolSize;
    }

    public String toUri() {
        return "redis://" + host + ":" + port;
    }
}
