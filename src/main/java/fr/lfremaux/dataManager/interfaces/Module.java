package fr.lfremaux.dataManager.interfaces;

import java.io.IOException;

public interface Module {

    void init(Plugin plugin) throws IOException;

    void close();
}
