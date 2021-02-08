package fr.lfremaux.dataManager;

import fr.lfremaux.dataManager.exceptions.InvalidConfigTypeException;
import fr.lfremaux.dataManager.exceptions.ModuleNotInitializedException;
import fr.lfremaux.dataManager.interfaces.CustomConfig;
import fr.lfremaux.dataManager.interfaces.Module;
import fr.lfremaux.dataManager.interfaces.Plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private final Plugin plugin;

    private final Map<Class<? extends Module>, Module> modules = new HashMap<>();

    public DataManager(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Init module
     *
     * @param moduleClass module type
     * @return this
     */
    public DataManager with(Class<? extends Module> moduleClass) throws IOException {
        try {
            return with(moduleClass, null);
        } catch (InvalidConfigTypeException ignored) {
        }
        return this;
    }

    /**
     * Init module
     *
     * @param moduleClass module type
     * @param config      provide a custom configuration
     * @return this
     */
    public DataManager with(Class<? extends Module> moduleClass, CustomConfig config) throws IOException, InvalidConfigTypeException {
        final Module module;
        try {
            module = moduleClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        module.init(plugin, config);
        modules.put(moduleClass, module);
        return this;
    }

    /**
     * Get connection module
     *
     * @param classType module type
     * @return Module
     * @throws ModuleNotInitializedException if module not found or not initialized
     */
    public Module getModule(Class<? extends Module> classType) throws ModuleNotInitializedException {
        if (!modules.containsKey(classType))
            throw new ModuleNotInitializedException(classType.getName() + " module not initialized");
        return modules.get(classType);
    }

    /**
     * Close each connection
     */
    public void close() {
        modules.values().forEach(Module::close);
    }
}
