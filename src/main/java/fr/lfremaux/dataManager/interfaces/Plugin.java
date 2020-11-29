package fr.lfremaux.dataManager.interfaces;

import java.io.File;
import java.util.logging.Level;

public interface Plugin {

    String getName();

    default void log(String message) {
        log(Level.INFO, message);
    }

    void log(Level level, String message);

    File getConfigFolder();
}
