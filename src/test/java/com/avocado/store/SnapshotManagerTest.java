package com.avocado.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotManagerTest {

    @TempDir
    Path tempDir;

    private InMemoryObjectStore store;
    private SnapshotManager snapshotManager;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        store = new InMemoryObjectStore();
        objectMapper = new ObjectMapper();

        DataStoreConfiguration config = new DataStoreConfiguration();
        config.setDataDir(tempDir.toString());

        snapshotManager = new SnapshotManager(store, config, objectMapper);
    }

    @Test
    void saveCollection_writesJsonFileToDataDir() throws IOException {
        store.put("users", "alice", TextNode.valueOf("Alice"));
        store.put("users", "bob", TextNode.valueOf("Bob"));

        snapshotManager.saveCollection("users");

        File snapshot = tempDir.resolve("users.json").toFile();
        assertTrue(snapshot.exists(), "Snapshot file should be created");

        Map<String, JsonNode> loaded = objectMapper.readValue(snapshot,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, JsonNode.class));
        assertEquals(2, loaded.size());
        assertEquals("Alice", loaded.get("alice").asText());
        assertEquals("Bob", loaded.get("bob").asText());
    }

    @Test
    void loadAll_populatesStoreFromDiskFiles() throws IOException {
        Map<String, String> raw = Map.of("key1", "value1", "key2", "value2");
        objectMapper.writeValue(tempDir.resolve("items.json").toFile(), raw);

        snapshotManager.loadAll();

        Map<String, JsonNode> loaded = store.getCollection("items");
        assertEquals(2, loaded.size());
        assertEquals("value1", loaded.get("key1").asText());
        assertEquals("value2", loaded.get("key2").asText());
    }

    @Test
    void loadAll_skipsCorruptedFiles() throws IOException {
        Files.writeString(tempDir.resolve("bad.json"), "{ this is not valid json }}}");
        objectMapper.writeValue(tempDir.resolve("good.json").toFile(), Map.of("k", "v"));

        assertDoesNotThrow(() -> snapshotManager.loadAll());

        assertTrue(store.getCollection("bad").isEmpty(), "Corrupted collection should not be loaded");
        assertFalse(store.getCollection("good").isEmpty(), "Valid collection should still be loaded");
    }

    @Test
    void loadAll_doesNothingWhenDataDirIsEmpty() {
        assertDoesNotThrow(() -> snapshotManager.loadAll());
        assertTrue(store.getAllCollections().isEmpty());
    }

    @Test
    void saveAll_persistsAllCollections() throws IOException {
        store.put("products", "p1", TextNode.valueOf("Widget"));
        store.put("orders", "o1", TextNode.valueOf("Order1"));

        snapshotManager.saveAll();

        assertTrue(tempDir.resolve("products.json").toFile().exists());
        assertTrue(tempDir.resolve("orders.json").toFile().exists());
    }

    @Test
    void roundtrip_saveAndLoad_preservesData() {
        store.put("sessions", "s1", TextNode.valueOf("session-data-1"));
        store.put("sessions", "s2", TextNode.valueOf("session-data-2"));

        snapshotManager.saveAll();

        InMemoryObjectStore freshStore = new InMemoryObjectStore();
        DataStoreConfiguration config = new DataStoreConfiguration();
        config.setDataDir(tempDir.toString());
        SnapshotManager freshManager = new SnapshotManager(freshStore, config, objectMapper);
        freshManager.loadAll();

        Map<String, JsonNode> sessions = freshStore.getCollection("sessions");
        assertEquals(2, sessions.size());
        assertEquals("session-data-1", sessions.get("s1").asText());
        assertEquals("session-data-2", sessions.get("s2").asText());
    }

    @Test
    void saveCollection_createsDataDirIfMissing() {
        DataStoreConfiguration config = new DataStoreConfiguration();
        config.setDataDir(tempDir.resolve("nested/subdir").toString());
        SnapshotManager manager = new SnapshotManager(store, config, objectMapper);

        store.put("col", "k", TextNode.valueOf("v"));

        assertDoesNotThrow(() -> manager.saveCollection("col"));
        assertTrue(tempDir.resolve("nested/subdir/col.json").toFile().exists());
    }
}
