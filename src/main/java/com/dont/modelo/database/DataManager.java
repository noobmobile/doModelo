package com.dont.modelo.database;

import com.dont.modelo.database.datasources.IDataSource;
import com.dont.modelo.models.Manager;
import com.dont.modelo.models.database.Storable;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataManager extends Manager {

    private final IDataSource dataSource;
    private final HashMap<String, Storable> cache;

    public DataManager(IDataSource dataSource) {
        super(null);
        this.dataSource = dataSource;
        this.cache = new HashMap<>();
    }

    public <T extends Storable> T get(String key) {
        return (T) cache.get(key);
    }

    public void cache(Storable storable) {
        cache.put(storable.getKey(), storable);
    }

    public void uncache(String key) {
        cache.remove(key);
    }

    public boolean isCached(String key) {
        return cache.containsKey(key);
    }

    public Collection<Storable> getCached() {
        return cache.values();
    }

    public <T extends Storable> List<T> getCached(Class<T> clazz) {
        return cache.values().stream().filter(storable -> clazz.isAssignableFrom(storable.getClass())).map(clazz::cast).collect(Collectors.toList());

    }

    /**
     * @return storables que não estão no cache, modificar-los não irá alterar nada
     */
    public <T extends Storable> List<T> getNonCached(Class<T> clazz) {
        List<T> storables = dataSource.getAll(clazz);
        storables.removeIf(storable -> cache.containsKey(storable.getKey()));
        return storables;
    }

    /**
     * @return storables offlines mais os que estão no cache
     */
    public <T extends Storable> List<T> getAll(Class<T> clazz) {
        List<T> cached = getCached(clazz);
        List<T> nonCached = getNonCached(clazz);
        return Stream.of(cached, nonCached).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public void saveCached() {
        getCached().forEach(storable -> dataSource.insert(storable, false));
    }

    public void loadOnline() {
        Utils.measureTime(() -> {
            int loaded = Bukkit.getOnlinePlayers().size();
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (dataSource.exists(player.getName())) {
                    User user = dataSource.find(player.getName(), User.class);
                    cache(user);
                    Utils.debug(Utils.LogType.DEBUG, "puxando player " + player.getName() + " da tabela");
                } else {
                    User user = new User(player.getName());
                    cache(user);
                    Utils.debug(Utils.LogType.DEBUG, "criando player " + player.getName() + " na tabela");
                }
            });
            return "Lido {x} usuários onlines em {time}".replace("{x}", String.valueOf(loaded));
        });
    }

    public void loadAll() {
        Utils.measureTime(() -> {
            List<User> users = getNonCached(User.class);
            users.forEach(this::cache);
            return "Lido todos {x} usuários em {time}".replace("{x}", String.valueOf(users.size()));
        });
    }

    public IDataSource getDataSource() {
        return dataSource;
    }

}
