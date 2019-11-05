package fr.thefoxy41.dataManager.config.helpers;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Configs {

    /**
     * get YAMLConfiguration from file
     *
     * @param file File
     * @return YAMLConfiguration
     */
    public static YAMLConfiguration getConfiguration(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            YAMLConfiguration configuration = new YAMLConfiguration();
            configuration.read(inputStream);
            return configuration;
        } catch (IOException | ConfigurationException e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * save YAMLConfiguration to specified file
     *
     * @param configuration YAMLConfiguration
     * @param file          File
     */
    public static void save(YAMLConfiguration configuration, File file) {
        Writer writer = null;
        try {
            Files.createIfEmpty(file);

            writer = new FileWriter(file);
            configuration.write(writer);
            writer.flush();
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isValid(String modelName, YAMLConfiguration config) {
        YAMLConfiguration defaultConfig = Configs.getResourceConfig(modelName);

        List<String> keys = new ArrayList<>();

        // add default keys to list
        Iterator<String> defaultKeys = defaultConfig.getKeys();
        while (defaultKeys.hasNext()) {
            String key = defaultKeys.next();
            keys.add(key);
        }

        // check each config key
        Iterator<String> configKeys = config.getKeys();
        while (configKeys.hasNext()) {
            String key = configKeys.next();
            if (!keys.contains(key)) return false;
        }

        return true;
    }

    public static YAMLConfiguration getResourceConfig(String name) {
        InputStream stream = Configs.class.getClassLoader().getResourceAsStream(name + (name.endsWith(".yml") ? "" : ".yml"));
        YAMLConfiguration configuration = new YAMLConfiguration();

        if (stream != null) {
            try {
                configuration.read(stream);
                stream.close();
            } catch (IOException | ConfigurationException e) {
                e.printStackTrace();
            }
        }

        return configuration;
    }
}