package fr.thefoxy41.dataManager.config.helpers;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Configs {

    /**
     * save YAMLConfiguration to specified file
     *
     * @param configuration YAMLConfiguration
     * @param file          File
     */
    public static void save(InputStream configuration, File file) {
        try {
            file.mkdirs();
            java.nio.file.Files.copy(configuration, Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValid(String modelName, InputStream config) {
        InputStream defaultConfig = Configs.getResourceConfig(modelName);
        try {
            int ch = defaultConfig.read();
            while (ch != -1) {
                ch = defaultConfig.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        List<String> keys = new ArrayList<>();

        // add default keys to list
        Iterator<String> defaultKeys = defaultConfig.();
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
*/
        return true;
    }

    public static InputStream getResourceConfig(String name) {
        return Configs.class.getClassLoader().getResourceAsStream(name + (name.endsWith(".yml") ? "" : ".yml"));
    }
}