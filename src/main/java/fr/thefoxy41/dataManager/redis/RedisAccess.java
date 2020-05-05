package fr.thefoxy41.dataManager.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.FstCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

public class RedisAccess {
    private final RedisCredentials credentials;
    private final RedissonClient redissonClient;

    public RedisAccess(RedisCredentials credentials) {
        this.credentials = credentials;
        this.redissonClient = setupRedisson(credentials);
    }

    public RedissonClient setupRedisson(RedisCredentials credentials) {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.setCodec(new FstCodec());
        config.setThreads(credentials.getThreads());
        config.setNettyThreads(credentials.getNettyThreads());
        config.useSingleServer()
                .setAddress(credentials.toUri())
                .setPassword(credentials.getPassword())
                .setDatabase(credentials.getDatabaseId())
                .setRetryInterval(1000)
                .setRetryAttempts(5)
                .setSubscriptionConnectionMinimumIdleSize(credentials.getMinimumSubscriptionIdle())
                .setSubscriptionConnectionPoolSize(credentials.getSubscriptionPoolSize())
                .setConnectionMinimumIdleSize(credentials.getMinimumIdle())
                .setConnectionPoolSize(credentials.getPoolSize())
                .setClientName(credentials.getClientName());

        return Redisson.create(config);
    }

    public void close() {
        getRedissonClient().shutdown();
    }

    public RedisCredentials getCredentials() {
        return credentials;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }
}
