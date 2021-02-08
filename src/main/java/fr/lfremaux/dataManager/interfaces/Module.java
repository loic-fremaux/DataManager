package fr.lfremaux.dataManager.interfaces;

import fr.lfremaux.dataManager.exceptions.InvalidConfigTypeException;

import java.io.IOException;

public interface Module {

    void init(Plugin plugin, CustomConfig config) throws IOException, InvalidConfigTypeException;

    void close();
}
