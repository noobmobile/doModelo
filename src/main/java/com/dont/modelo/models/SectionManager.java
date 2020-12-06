package com.dont.modelo.models;

import com.dont.modelo.utils.SectionBuilder;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public abstract class SectionManager<K, V> extends Manager {
    private Function<V, K> extractor;
    private Map<K, V> map;

    public SectionManager(AbstractTerminal main, SectionBuilder<V> sectionBuilder, Function<V, K> extractor) {
        super(main);
        this.extractor = extractor;
        this.map = sectionBuilder.build(extractor);
    }

    public V get(K key) {
        return map.get(key);
    }

    public Collection<V> getAll() {
        return map.values();
    }

    public Function<V, K> getExtractor() {
        return extractor;
    }
}
