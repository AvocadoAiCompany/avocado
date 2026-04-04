package com.avocado.store;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryObjectStore {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, JsonNode>> data = new ConcurrentHashMap<>();

    public void put(String collection, String key, JsonNode value) {
        data.computeIfAbsent(collection, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    public JsonNode get(String collection, String key) {
        var col = data.get(collection);
        return col == null ? null : col.get(key);
    }

    public Map<String, JsonNode> getAll(String collection) {
        return data.getOrDefault(collection, new ConcurrentHashMap<>());
    }

    public Set<String> collections() {
        return data.keySet();
    }
}
