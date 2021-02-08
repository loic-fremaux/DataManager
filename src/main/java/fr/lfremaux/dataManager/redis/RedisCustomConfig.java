package fr.lfremaux.dataManager.redis;

import fr.lfremaux.dataManager.interfaces.CustomConfig;
import org.redisson.config.Config;

import java.util.function.Predicate;

public class RedisCustomConfig extends Config implements CustomConfig {

    private final Predicate<String> filter;

    public RedisCustomConfig(Predicate<String> nameFilter) {
        this.filter = nameFilter;
    }

   public boolean matches(String name) {
        return filter.test(name);
   }
}
