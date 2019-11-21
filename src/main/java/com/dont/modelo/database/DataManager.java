package com.dont.modelo.database;

import com.dont.modelo.models.database.Storable;
import com.dont.modelo.models.database.User;
import com.dont.modelo.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataManager{

    private final IDataSource dataSource;
    private final HashMap<String, Storable> cache;
    public DataManager(IDataSource dataSource){
        this.dataSource = dataSource;
        this.cache = new HashMap<>();
    }

    public <T extends Storable> T get(String key){
        return (T) cache.get(key);
    }

    public void cache(Storable storable){
        cache.put(storable.getName(), storable);
    }

    public void uncache(String key){
        cache.remove(key);
    }

    public boolean isCached(String key){
        return cache.containsKey(key);
    }

    public Collection<Storable> getCached(){
        return cache.values();
    }

    /**
     *
     * @return storables que não estão no cache, modificar-los não irá alterar nada
     */
    public <T extends Storable> List<T> getNonCached(Class<T> clazz){
        List<T> storables = dataSource.getAll(clazz);
        storables.removeIf(storable -> cache.containsKey(storable.getName()));
        return storables;
    }

    /**
     *
     * @return storables offlines mais os que estão no cache
     */
    public <T extends Storable>  List<T> getAll(Class<T> clazz){
        List<T> cached = getCached().stream().filter(storable -> clazz.isAssignableFrom(storable.getClass())).map(clazz::cast).collect(Collectors.toList());
        List<T> nonCached = getNonCached(clazz);
        return Stream.of(cached, nonCached).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public IDataSource getDataSource() {
        return dataSource;
    }

    public void deleteOldUsers(){
        Utils.measureTime("deletado usuarios em <tempo>ms", () -> {
            int deletado = 0;
            for (User user : getNonCached(User.class)){
                if (user.canBeDeleted()){
                    Utils.debug(Utils.LogType.DEBUG, "deletado "+user.getName()+"");
                    dataSource.delete(user.getName(), true);
                    deletado++;
                }
            }
            Utils.debug(Utils.LogType.DEBUG, "deletado "+deletado+" usuarios");
        });
    }

}
