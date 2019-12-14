package fr.thefoxy41.dataManager.redis;

import fr.thefoxy41.dataManager.auth.Credentials;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisAccess {
    private RedisCredentials credentials;
    private RedissonClient redissonClient;

    public RedisAccess(RedisCredentials credentials) {
        this.credentials = credentials;
        this.redissonClient = setupRedisson(credentials);
    }

    public RedissonClient setupRedisson(RedisCredentials credentials) {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        config.setThreads(12);
        config.setNettyThreads(12);
        config.useSingleServer()
                .setAddress(credentials.toUri())
                .setPassword(credentials.getPassword())
                .setDatabase(credentials.getDatabaseId())
                .setRetryInterval(1000)
                .setRetryAttempts(5)
                .setConnectionMinimumIdleSize(Credentials.DEFAULT_POOL_SIZE)
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
