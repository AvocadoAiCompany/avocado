package com.avocado.store;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Optional;

public interface ObjectStore {
    void put(String collection, String key, JsonNode value);
    Optional<JsonNode> get(String collection, String key);
    void delete(String collection, String key);
    Map<String, JsonNode> getAll(String collection);
}
