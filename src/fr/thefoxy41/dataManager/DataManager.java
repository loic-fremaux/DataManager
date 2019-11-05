package fr.thefoxy41.dataManager;

import fr.thefoxy41.dataManager.exceptions.ModuleNotInitializedException;
import fr.thefoxy41.dataManager.interfaces.Module;
import fr.thefoxy41.dataManager.interfaces.Plugin;

import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private Plugin plugin;

    private Map<Class<? extends Module>, Module> modules = new HashMap<>();

    public DataManager(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Init module
     *
     * @param moduleClass module type
     * @return this
     */
    public DataManager with(Class<? extends Module> moduleClass) {
        try {
            Module module = moduleClass.newInstance();
            module.init(plugin);
            modules.put(moduleClass, module);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

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
