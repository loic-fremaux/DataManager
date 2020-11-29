package fr.lfremaux.dataManager.redis;

import fr.lfremaux.dataManager.auth.Credentials;

public class RedisCredentials extends Credentials {
    private int databaseId;
    private int subscriptionPoolSize;
    private int minimumSubscriptionIdle;
    private int threads;
    private int nettyThreads;

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

    public int getThreads() {
        return threads;
    }

    public int getNettyThreads() {
        return nettyThreads;
    }

    public String toUri() {
        return "redis://" + host + ":" + port;
    }
}
