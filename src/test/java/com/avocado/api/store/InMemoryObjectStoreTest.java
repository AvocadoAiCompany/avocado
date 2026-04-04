package com.avocado.api.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryObjectStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private InMemoryObjectStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryObjectStore();
    }

    @Test
    void putAndGet_returnsStoredValue() {
        JsonNode value = MAPPER.createObjectNode().put("name", "avocado");
        store.put("fruits", "1", value);

        Optional<JsonNode> result = store.get("fruits", "1");

        assertTrue(result.isPresent());
        assertEquals(value, result.get());
    }

    @Test
    void get_missingKey_returnsEmpty() {
        Optional<JsonNode> result = store.get("fruits", "missing");

        assertTrue(result.isEmpty());
    }

    @Test
    void get_missingCollection_returnsEmpty() {
        Optional<JsonNode> result = store.get("nonexistent", "1");

        assertTrue(result.isEmpty());
    }

    @Test
    void delete_removesEntry() {
        JsonNode value = MAPPER.createObjectNode().put("name", "avocado");
        store.put("fruits", "1", value);

        store.delete("fruits", "1");

        assertTrue(store.get("fruits", "1").isEmpty());
    }

    @Test
    void delete_missingKey_doesNotThrow() {
        assertDoesNotThrow(() -> store.delete("fruits", "nonexistent"));
    }

    @Test
    void delete_missingCollection_doesNotThrow() {
        assertDoesNotThrow(() -> store.delete("nonexistent", "1"));
    }

    @Test
    void getAll_returnsAllEntriesInCollection() {
        JsonNode v1 = MAPPER.createObjectNode().put("type", "hass");
        JsonNode v2 = MAPPER.createObjectNode().put("type", "fuerte");
        store.put("fruits", "1", v1);
        store.put("fruits", "2", v2);

        Map<String, JsonNode> result = store.getAll("fruits");

        assertEquals(2, result.size());
        assertEquals(v1, result.get("1"));
        assertEquals(v2, result.get("2"));
    }

    @Test
    void getAll_emptyCollection_returnsEmptyMap() {
        Map<String, JsonNode> result = store.getAll("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_returnsDefensiveCopy() {
        JsonNode value = MAPPER.createObjectNode().put("name", "avocado");
        store.put("fruits", "1", value);

        Map<String, JsonNode> snapshot = store.getAll("fruits");
        store.put("fruits", "2", MAPPER.createObjectNode().put("name", "mango"));

        assertEquals(1, snapshot.size(), "snapshot should not reflect subsequent mutations");
    }

    @Test
    void collectionsAreIsolated() {
        JsonNode v1 = MAPPER.createObjectNode().put("name", "avocado");
        JsonNode v2 = MAPPER.createObjectNode().put("name", "carrot");
        store.put("fruits", "1", v1);
        store.put("vegetables", "1", v2);

        assertEquals(v1, store.get("fruits", "1").orElseThrow());
        assertEquals(v2, store.get("vegetables", "1").orElseThrow());
        assertEquals(1, store.getAll("fruits").size());
        assertEquals(1, store.getAll("vegetables").size());
    }
}
