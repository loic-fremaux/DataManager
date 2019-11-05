package fr.thefoxy41.dataManager.mysql;

import fr.thefoxy41.dataManager.config.helpers.Configs;
import fr.thefoxy41.dataManager.exceptions.InvalidAccessException;
import fr.thefoxy41.dataManager.interfaces.Module;
import fr.thefoxy41.dataManager.interfaces.Plugin;
import org.apache.commons.configuration2.YAMLConfiguration;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class MysqlManager implements Module {
    private Plugin plugin = null;
    private static final int DEFAULT_POOL_SIZE = 4;
    private static Map<String, MysqlAccess> mysqlAccess = new HashMap<>();

    public static final String DEFAULT_CONFIG = "mysql.yml";

    public MysqlAccess getAccess(String name) throws InvalidAccessException {
        if (!mysqlAccess.containsKey(name)) throw new InvalidAccessException("MySQL access '" + name + "' not found");
        return mysqlAccess.get(name);
    }

    public void init(Plugin plugin) {
        this.plugin = plugin;
        String configsPath = plugin.getConfigFolder().getPath() + "/mysql";

        File file = new File(configsPath);
        if (!file.exists()) {
            Configs.save(
                    Configs.getResourceConfig(DEFAULT_CONFIG),
                    new File(file.getPath(), "default.yml")
            );

            plugin.log("Default mysql configuration moved to " + file.getPath() + ".");
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
            String user = config.getString("user");
            String dbName = config.getString("database_name");
            int port = config.getInt("port");
            int poolSize = config.containsKey("poolSize") ? config.getInt("pool_size") : DEFAULT_POOL_SIZE;
            if (poolSize < 1 || poolSize > 100) {
                throw new InvalidParameterException("Pool size must be an integer between 1 and 100");
            }

            MysqlCredentials credentials = new MysqlCredentials(
                    host,
                    user,
                    config.getString("password"),
                    dbName,
                    plugin.getName() + "_" + this.getClass().getName() + "_client",
                    port,
                    poolSize
            );

            MysqlAccess access = new MysqlAccess(credentials);
            plugin.log("New connection established to " + host + " on port " + port + " with user " + user + " on database " + dbName + ".");

            mysqlAccess.put(dbName, access);
        }
    }

    public void close() {
        for (MysqlAccess access : mysqlAccess.values()) {
            access.closePool();
            plugin.log("Connection to " + access.getCredentials().getHost() + " closed.");
        }
    }
}
