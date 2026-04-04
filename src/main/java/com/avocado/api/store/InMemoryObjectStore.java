package com.avocado.api.store;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryObjectStore {

    private final ConcurrentHashMap<String, byte[]> data = new ConcurrentHashMap<>();

    public void put(String key, byte[] value) {
        data.put(key, value);
    }

    public Optional<byte[]> get(String key) {
        return Optional.ofNullable(data.get(key));
    }

    public void delete(String key) {
        data.remove(key);
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(data.keySet());
    }

    /** Package-private: used by SnapshotManager to capture current state. */
    Map<String, byte[]> snapshot() {
        return new HashMap<>(data);
    }

    /** Package-private: used by SnapshotManager to restore persisted state. */
    void restore(Map<String, byte[]> snapshot) {
        data.clear();
        data.putAll(snapshot);
    }
}
