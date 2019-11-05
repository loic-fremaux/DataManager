package fr.thefoxy41.dataManager.redis;

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
        config.setUseLinuxNativeEpoll(true);
        config.setThreads(4);
        config.setNettyThreads(4);
        config.useSingleServer()
                .setAddress(credentials.toUri())
                .setPassword(credentials.getPassword())
                .setDatabase(credentials.getDatabaseId())
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
