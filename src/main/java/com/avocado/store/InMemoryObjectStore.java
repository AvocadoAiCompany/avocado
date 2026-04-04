package com.avocado.store;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryObjectStore implements ObjectStore {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, JsonNode>> registry =
            new ConcurrentHashMap<>();

    @Override
    public void put(String collection, String key, JsonNode value) {
        registry.computeIfAbsent(collection, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    @Override
    public Optional<JsonNode> get(String collection, String key) {
        ConcurrentHashMap<String, JsonNode> col = registry.get(collection);
        if (col == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(col.get(key));
    }

    @Override
    public void delete(String collection, String key) {
        ConcurrentHashMap<String, JsonNode> col = registry.get(collection);
        if (col != null) {
            col.remove(key);
        }
    }

    @Override
    public Map<String, JsonNode> getAll(String collection) {
        ConcurrentHashMap<String, JsonNode> col = registry.get(collection);
        if (col == null) {
            return Map.of();
        }
        return new HashMap<>(col);
    }
}
