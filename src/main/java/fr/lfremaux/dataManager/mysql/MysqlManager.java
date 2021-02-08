package fr.lfremaux.dataManager.mysql;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.zaxxer.hikari.HikariConfig;
import fr.lfremaux.dataManager.exceptions.InvalidAccessException;
import fr.lfremaux.dataManager.config.helpers.Configs;
import fr.lfremaux.dataManager.exceptions.InvalidConfigTypeException;
import fr.lfremaux.dataManager.interfaces.CustomConfig;
import fr.lfremaux.dataManager.interfaces.Module;
import fr.lfremaux.dataManager.interfaces.Plugin;
import org.redisson.config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MysqlManager implements Module {
    private Plugin plugin = null;

    private static final Map<String, MysqlAccess> mysqlAccess = new HashMap<>();

    public static final String DEFAULT_CONFIG = "mysql.yml";

    public MysqlAccess getAccess(String name) throws InvalidAccessException {
        if (!mysqlAccess.containsKey(name)) throw new InvalidAccessException("MySQL access '" + name + "' not found");
        return mysqlAccess.get(name);
    }

    public void init(Plugin plugin, CustomConfig config) throws IOException, InvalidConfigTypeException {
        if (config != null) {
            if (!(config instanceof HikariConfig)) {
                throw new InvalidConfigTypeException("Configuration should extend com.zaxxer.hikari.HikariConfig");
            }
        }

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
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            InputStream input = new FileInputStream(fileConfig);

            MysqlCredentials credentials = mapper.readValue(input, MysqlCredentials.class);
            String clientName = plugin.getName() + "_" + this.getClass().getName() + "_client";
            credentials.setClientName(clientName);

            String name = fileConfig.getName().replace(".yml", "");
            MysqlAccess access;

            if (config != null && config.matches(name)) {
                access = new MysqlAccess(credentials, (MysqlCustomConfig) config);
            } else {
                access = new MysqlAccess(credentials);
            }

            plugin.log("New connection established"
                    + " to " + credentials.getHost()
                    + " on port " + credentials.getPort()
                    + " with user " + credentials.getUser()
                    + " on database " + credentials.getDbName()
                    + "."
            );

            mysqlAccess.put(name, access);

            try {
                Connection connection = access.getConnection();
                if (connection == null) throw new IOException("Connection not available");
                connection.close();
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }
    }

    public void close() {
        for (MysqlAccess access : mysqlAccess.values()) {
            access.closePool();
            plugin.log("Connection to " + access.getCredentials().getHost() + " closed.");
        }
    }
}
