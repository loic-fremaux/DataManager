package fr.thefoxy41.dataManager.redis;

import fr.thefoxy41.dataManager.config.helpers.Configs;
import fr.thefoxy41.dataManager.exceptions.InvalidAccessException;
import fr.thefoxy41.dataManager.interfaces.Module;
import fr.thefoxy41.dataManager.interfaces.Plugin;
import org.apache.commons.configuration2.YAMLConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RedisManager implements Module {
    private Plugin plugin = null;
    private static final int DEFAULT_POOL_SIZE = 4;
    private static Map<Integer, RedisAccess> redisAccess = new HashMap<>();

    public static final String DEFAULT_CONFIG = "redis.yml";

    public RedisAccess getAccess(int id) throws InvalidAccessException {
        if (!redisAccess.containsKey(id)) throw new InvalidAccessException("Redis access " + id + " not found");
        return redisAccess.get(id);
    }

    public void init(Plugin plugin) {
        this.plugin = plugin;
        String configsPath = plugin.getConfigFolder().getPath() + "/redis";

        File file = new File(configsPath);
        if (!file.exists()) {
            Configs.save(
                    Configs.getResourceConfig(DEFAULT_CONFIG),
                    new File(file.getPath(), "default.yml")
            );

            plugin.log("Default redis configuration moved to " + file.getPath() + ".");
        }

        File[] files = file.listFiles();
        if (files == null) return;

        for (File fileConfig : files) {
            YAMLConfiguration config = Configs.getConfiguration(fileConfig);

            if (config == null || !Configs.isValid(DEFAULT_CONFIG, config)) {
                plugin.log("Skipped " + fileConfig.getName() + ": configuration is not valid (missing keys)");
                continue;
            }

            String host = config.getString("host");
            int dbId = config.getInt("database_id");
            int port = config.getInt("port");

            RedisCredentials credentials = new RedisCredentials(
                    host,
                    config.getString("password"),
                    port,
                    dbId,
                    plugin.getName() + "_" + this.getClass().getName() + "_client"
            );

            RedisAccess access = new RedisAccess(credentials);
            plugin.log("New connection established to " + host + " on port " + port + " on redis cache " + dbId + ".");

            redisAccess.put(dbId, access);
        }
    }

    public void close() {
        for (RedisAccess access : redisAccess.values()) {
            access.close();
            plugin.log("Connection to " + access.getCredentials().getHost() + " closed.");
        }
    }
}
