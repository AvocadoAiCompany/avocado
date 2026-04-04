package com.avocado.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotManagerTest {

    @TempDir
    Path tempDir;

    private ObjectMapper mapper;
    private InMemoryObjectStore store;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        store = new InMemoryObjectStore();
    }

    @Test
    void constructorCreatesDataDirIfAbsent() throws IOException {
        Path newDir = tempDir.resolve("snapshots/nested");
        assertFalse(Files.exists(newDir));
        new SnapshotManager(newDir.toString(), store, mapper);
        assertTrue(Files.isDirectory(newDir));
    }

    @Test
    void saveCollectionWritesJsonFile() throws IOException {
        store.put("fruits", "apple", TextNode.valueOf("red"));
        store.put("fruits", "banana", TextNode.valueOf("yellow"));

        SnapshotManager sm = new SnapshotManager(tempDir.toString(), store, mapper);
        sm.saveCollection("fruits");

        Path snapshot = tempDir.resolve("fruits.json");
        assertTrue(Files.exists(snapshot));
        Map<String, JsonNode> loaded = mapper.readValue(snapshot.toFile(),
                mapper.getTypeFactory().constructMapType(Map.class, String.class, JsonNode.class));
        assertEquals(TextNode.valueOf("red"), loaded.get("apple"));
        assertEquals(TextNode.valueOf("yellow"), loaded.get("banana"));
    }

    @Test
    void saveCollectionOverwritesPreviousFile() throws IOException {
        store.put("col", "k", TextNode.valueOf("v1"));
        SnapshotManager sm = new SnapshotManager(tempDir.toString(), store, mapper);
        sm.saveCollection("col");

        store.put("col", "k", TextNode.valueOf("v2"));
        sm.saveCollection("col");

        Map<String, JsonNode> loaded = mapper.readValue(tempDir.resolve("col.json").toFile(),
                mapper.getTypeFactory().constructMapType(Map.class, String.class, JsonNode.class));
        assertEquals(TextNode.valueOf("v2"), loaded.get("k"));
    }

    @Test
    void saveAllPersistsEveryCollection() throws IOException {
        store.put("a", "x", TextNode.valueOf("1"));
        store.put("b", "y", TextNode.valueOf("2"));

        SnapshotManager sm = new SnapshotManager(tempDir.toString(), store, mapper);
        sm.saveAll();

        assertTrue(Files.exists(tempDir.resolve("a.json")));
        assertTrue(Files.exists(tempDir.resolve("b.json")));
    }

    @Test
    void loadAllRestoresCollectionsIntoStore() throws IOException {
        Map<String, String> data = Map.of("key1", "val1", "key2", "val2");
        mapper.writeValue(tempDir.resolve("widgets.json").toFile(), data);

        SnapshotManager sm = new SnapshotManager(tempDir.toString(), store, mapper);
        sm.loadAll();

        assertEquals(TextNode.valueOf("val1"), store.get("widgets", "key1"));
        assertEquals(TextNode.valueOf("val2"), store.get("widgets", "key2"));
    }

    @Test
    void loadAllDeriveCollectionNameFromFilename() throws IOException {
        mapper.writeValue(tempDir.resolve("orders.json").toFile(), Map.of("o1", "pending"));

        SnapshotManager sm = new SnapshotManager(tempDir.toString(), store, mapper);
        sm.loadAll();

        assertNotNull(store.get("orders", "o1"));
    }

    @Test
    void loadAllSkipsCorruptedFileAndContinues() throws IOException {
        Files.writeString(tempDir.resolve("bad.json"), "NOT_VALID_JSON{{{");
        mapper.writeValue(tempDir.resolve("good.json").toFile(), Map.of("g", "ok"));

        SnapshotManager sm = new SnapshotManager(tempDir.toString(), store, mapper);
        assertDoesNotThrow(sm::loadAll);

        assertNotNull(store.get("good", "g"));
        assertNull(store.get("bad", "anything"));
    }

    @Test
    void loadAllIgnoresNonJsonFiles() throws IOException {
        Files.writeString(tempDir.resolve("readme.txt"), "not json");
        mapper.writeValue(tempDir.resolve("items.json").toFile(), Map.of("i", "v"));

        SnapshotManager sm = new SnapshotManager(tempDir.toString(), store, mapper);
        sm.loadAll();

        assertNotNull(store.get("items", "i"));
        assertNull(store.get("readme", "anything"));
    }

    @Test
    void saveAndLoadRoundTrip() throws IOException {
        store.put("session", "user1", mapper.readTree("{\"role\":\"admin\"}"));
        store.put("session", "user2", mapper.readTree("{\"role\":\"viewer\"}"));

        SnapshotManager sm = new SnapshotManager(tempDir.toString(), store, mapper);
        sm.saveAll();

        InMemoryObjectStore restored = new InMemoryObjectStore();
        SnapshotManager sm2 = new SnapshotManager(tempDir.toString(), restored, mapper);
        sm2.loadAll();

        assertEquals("admin", restored.get("session", "user1").get("role").asText());
        assertEquals("viewer", restored.get("session", "user2").get("role").asText());
    }
}
