package com.avocado.api.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotManagerTest {

    @TempDir
    Path tempDir;

    private InMemoryObjectStore store;
    private SnapshotManager snapshotManager;
    private Path snapshotFile;

    @BeforeEach
    void setUp() {
        store = new InMemoryObjectStore();
        snapshotFile = tempDir.resolve("snapshot.json");
        snapshotManager = new SnapshotManager(store, snapshotFile, new ObjectMapper());
    }

    @Test
    void loadAll_whenFileAbsent_storeRemainsEmpty() throws IOException {
        snapshotManager.loadAll();
        assertTrue(store.keys().isEmpty());
    }

    @Test
    void saveAndLoad_roundtripsData() throws IOException {
        store.put("hello", new byte[]{72, 101, 108, 108, 111});

        snapshotManager.saveAll();
        assertTrue(snapshotFile.toFile().exists());

        InMemoryObjectStore freshStore = new InMemoryObjectStore();
        SnapshotManager freshManager = new SnapshotManager(freshStore, snapshotFile, new ObjectMapper());
        freshManager.loadAll();

        assertArrayEquals(new byte[]{72, 101, 108, 108, 111}, freshStore.get("hello").get());
    }

    @Test
    void saveAll_createsParentDirectories() throws IOException {
        Path nested = tempDir.resolve("a/b/c/snapshot.json");
        SnapshotManager nestedManager = new SnapshotManager(store, nested, new ObjectMapper());
        store.put("k", new byte[]{1});

        nestedManager.saveAll();

        assertTrue(nested.toFile().exists());
    }

    @Test
    void saveAll_overwritesPreviousSnapshot() throws IOException {
        store.put("v1", new byte[]{1});
        snapshotManager.saveAll();

        store.restore(Map.of("v2", new byte[]{2}));
        snapshotManager.saveAll();

        InMemoryObjectStore freshStore = new InMemoryObjectStore();
        new SnapshotManager(freshStore, snapshotFile, new ObjectMapper()).loadAll();

        assertTrue(freshStore.get("v1").isEmpty());
        assertArrayEquals(new byte[]{2}, freshStore.get("v2").get());
    }
}
