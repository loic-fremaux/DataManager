package fr.lfremaux.dataManager.config.helpers;

import java.io.File;
import java.io.IOException;

public class Files {

    /**
     * create file if doesn't exist
     *
     * @param file File
     */
    public static boolean createIfEmpty(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
