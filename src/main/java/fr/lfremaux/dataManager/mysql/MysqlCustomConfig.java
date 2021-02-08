package fr.lfremaux.dataManager.mysql;

import com.zaxxer.hikari.HikariConfig;
import fr.lfremaux.dataManager.interfaces.CustomConfig;

import java.util.function.Predicate;

public class MysqlCustomConfig extends HikariConfig implements CustomConfig {

    private final Predicate<String> filter;

    public MysqlCustomConfig(Predicate<String> nameFilter) {
        this.filter = nameFilter;
    }

    public boolean matches(String name) {
        return filter.test(name);
    }
}
