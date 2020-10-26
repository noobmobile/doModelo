package com.dont.modelo.database.dao;

import com.dont.modelo.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CachedDao<K, V> extends GenericDao<K, V> {

    private final Map<K, V> cache;

    public CachedDao(GenericDao<K, V> other) {
        super(other);
        this.cache = new HashMap<>();
    }

    public void cache(K key, V value) {
        this.cache.put(key, value);
    }

    public void uncache(K key) {
        this.cache.remove(key);
    }

    public V getCached(K key) {
        return this.cache.get(key);
    }

    public boolean isCached(K key) {
        return this.cache.containsKey(key);
    }

    public void saveCached(boolean async) {
        this.cache.forEach((key, value) -> super.insert(key, value, async));
    }

    public Collection<V> getCached() {
        return cache.values();
    }

    public Collection<V> getNonCached() {
        List<V> values = super.findAll();
        values.removeIf(cache::containsValue);
        return values;
    }

    public Collection<V> getAll() {
        Collection<V> cached = getCached();
        Collection<V> nonCached = getNonCached();
        return Stream.of(cached, nonCached).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public void loadAll(Function<V, K> extractor) {
        Utils.measureTime(() -> {
            Collection<V> all = getNonCached();
            all.forEach(v -> this.cache(extractor.apply(v), v));
            return "Carregado " + all.size() + " objetos em {time}";
        });
    }

}
