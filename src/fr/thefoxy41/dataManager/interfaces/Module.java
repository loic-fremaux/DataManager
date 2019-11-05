package fr.thefoxy41.dataManager.interfaces;

import java.io.IOException;

public interface Module {

    void init(Plugin plugin) throws IOException;

    void close();
}
