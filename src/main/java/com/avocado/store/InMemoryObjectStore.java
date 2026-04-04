package com.avocado.store;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe, in-memory implementation of {@link ObjectStore} backed by a
 * {@link ConcurrentHashMap}.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public final class InMemoryObjectStore<K, V> implements ObjectStore<K, V> {

    private final Map<K, V> store = new ConcurrentHashMap<>();

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(value, "value must not be null");
        store.put(key, value);
    }

    @Override
    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "key must not be null");
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public boolean delete(K key) {
        Objects.requireNonNull(key, "key must not be null");
        return store.remove(key) != null;
    }

    @Override
    public boolean contains(K key) {
        Objects.requireNonNull(key, "key must not be null");
        return store.containsKey(key);
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(store.values());
    }

    @Override
    public int size() {
        return store.size();
    }
}
