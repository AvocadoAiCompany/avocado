package com.avocado.store;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryObjectStore {

    private final Map<String, Map<String, JsonNode>> collections = new ConcurrentHashMap<>();

    public void put(String collection, String key, JsonNode value) {
        collections.computeIfAbsent(collection, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    public Map<String, JsonNode> getCollection(String collection) {
        return Collections.unmodifiableMap(collections.getOrDefault(collection, Map.of()));
    }

    public Set<String> getAllCollections() {
        return Collections.unmodifiableSet(collections.keySet());
    }
}
