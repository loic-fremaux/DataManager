package fr.thefoxy41.dataManager.interfaces;

import java.io.File;
import java.util.logging.Level;

public interface Plugin {

    String getName();

    void log(String message);

    void log(Level level, String message);

    File getConfigFolder();
}
