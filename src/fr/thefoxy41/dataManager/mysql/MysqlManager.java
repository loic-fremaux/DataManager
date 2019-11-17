package fr.thefoxy41.dataManager.mysql;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.thefoxy41.dataManager.config.helpers.Configs;
import fr.thefoxy41.dataManager.exceptions.InvalidAccessException;
import fr.thefoxy41.dataManager.interfaces.Module;
import fr.thefoxy41.dataManager.interfaces.Plugin;

import java.io.*;
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

    public void init(Plugin plugin) throws IOException {
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
            MysqlAccess access = new MysqlAccess(credentials);

            plugin.log("New connection established"
                    + " to " + credentials.getHost()
                    + " on port " + credentials.getPort()
                    + " with user " + credentials.getUser()
                    + " on database " + credentials.getDatabase()
                    + "."
            );

            mysqlAccess.put(credentials.getDatabase(), access);
        }
    }

    public void close() {
        for (MysqlAccess access : mysqlAccess.values()) {
            access.closePool();
            plugin.log("Connection to " + access.getCredentials().getHost() + " closed.");
        }
    }
}
