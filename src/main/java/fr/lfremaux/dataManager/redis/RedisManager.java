package fr.lfremaux.dataManager.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.lfremaux.dataManager.config.helpers.Configs;
import fr.lfremaux.dataManager.exceptions.InvalidAccessException;
import fr.lfremaux.dataManager.exceptions.InvalidConfigTypeException;
import fr.lfremaux.dataManager.interfaces.CustomConfig;
import fr.lfremaux.dataManager.interfaces.Module;
import fr.lfremaux.dataManager.interfaces.Plugin;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RedisManager implements Module {
    private Plugin plugin = null;

    private static final Map<String, RedisAccess> redisAccess = new HashMap<>();

    public static final String DEFAULT_CONFIG = "redis.yml";

    public RedisAccess getAccess(String name) throws InvalidAccessException {
        if (!redisAccess.containsKey(name)) throw new InvalidAccessException("Redis access " + name + " not found");
        return redisAccess.get(name);
    }

    public void init(Plugin plugin, CustomConfig config) throws IOException, InvalidConfigTypeException {
        if (config != null) {
            if (!(config instanceof Config)) {
                throw new InvalidConfigTypeException("Configuration should extend org.redisson.config.Config");
            }
        }

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
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            InputStream input = new FileInputStream(fileConfig);

            RedisCredentials credentials = mapper.readValue(input, RedisCredentials.class);
            String clientName = plugin.getName() + "_" + this.getClass().getName() + "_client";
            credentials.setClientName(clientName);

            String name = fileConfig.getName().replace(".yml", "");
            RedisAccess access;

            if (config != null && config.matches(name)) {
                access = new RedisAccess(credentials, (RedisCustomConfig) config);
            } else {
                access = new RedisAccess(credentials);
            }

            plugin.log("New connection established"
                    + " to " + credentials.getHost()
                    + " on port " + credentials.getPort()
                    + " on redis cache "
                    + credentials.getDatabaseId() + "("
                    + name + ")."
            );

            redisAccess.put(name, access);
            RedissonClient client = access.getRedissonClient();
            if (client == null) throw new IOException("Connection not available");
        }
    }

    public void close() {
        for (RedisAccess access : redisAccess.values()) {
            access.close();
            plugin.log("Connection to " + access.getCredentials().getHost() + " closed.");
        }
    }
}
