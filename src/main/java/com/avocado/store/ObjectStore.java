package com.avocado.store;

import java.util.Collection;
import java.util.Optional;

/**
 * Generic key-value object store.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface ObjectStore<K, V> {

    /**
     * Store a value under the given key, replacing any existing entry.
     *
     * @param key   the key; must not be {@code null}
     * @param value the value to store; must not be {@code null}
     */
    void put(K key, V value);

    /**
     * Retrieve the value associated with the given key.
     *
     * @param key the key to look up
     * @return an {@link Optional} containing the value, or empty if absent
     */
    Optional<V> get(K key);

    /**
     * Remove the entry for the given key.
     *
     * @param key the key to remove
     * @return {@code true} if an entry was removed, {@code false} if the key was absent
     */
    boolean delete(K key);

    /**
     * Return {@code true} if the store contains an entry for the given key.
     *
     * @param key the key to test
     */
    boolean contains(K key);

    /**
     * Return all values currently held in the store.
     */
    Collection<V> values();

    /**
     * Return the number of entries in the store.
     */
    int size();
}
