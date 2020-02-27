package fr.thefoxy41.dataManager.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.thefoxy41.dataManager.config.helpers.Configs;
import fr.thefoxy41.dataManager.exceptions.InvalidAccessException;
import fr.thefoxy41.dataManager.interfaces.Module;
import fr.thefoxy41.dataManager.interfaces.Plugin;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RedisManager implements Module {
    private Plugin plugin = null;
    private static Map<String, RedisAccess> redisAccess = new HashMap<>();

    public static final String DEFAULT_CONFIG = "redis.yml";

    public RedisAccess getAccess(String name) throws InvalidAccessException {
        if (!redisAccess.containsKey(name)) throw new InvalidAccessException("Redis access " + name + " not found");
        return redisAccess.get(name);
    }

    public void init(Plugin plugin) throws IOException {
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
            RedisAccess access = new RedisAccess(credentials);
            String name = fileConfig.getName().replace(".yml", "");

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
