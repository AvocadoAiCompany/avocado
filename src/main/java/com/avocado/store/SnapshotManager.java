package com.avocado.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SnapshotManager {

    private static final Logger log = LoggerFactory.getLogger(SnapshotManager.class);
    private static final TypeReference<Map<String, JsonNode>> MAP_TYPE = new TypeReference<>() {};

    private final InMemoryObjectStore store;
    private final Path dataDir;
    private final ObjectMapper objectMapper;

    public SnapshotManager(InMemoryObjectStore store, DataStoreConfiguration config) {
        this(store, config, new ObjectMapper());
    }

    public SnapshotManager(InMemoryObjectStore store, DataStoreConfiguration config, ObjectMapper objectMapper) {
        this.store = store;
        this.dataDir = Path.of(config.getDataDir());
        this.objectMapper = objectMapper;
    }

    public void saveCollection(String collection) {
        ensureDataDirExists();
        File target = dataDir.resolve(collection + ".json").toFile();
        Map<String, JsonNode> data = store.getCollection(collection);
        try {
            objectMapper.writeValue(target, data);
            log.debug("Saved collection '{}' to {}", collection, target.getPath());
        } catch (IOException e) {
            log.error("Failed to save collection '{}' to {}: {}", collection, target.getPath(), e.getMessage(), e);
        }
    }

    public void loadAll() {
        ensureDataDirExists();
        File[] snapshots = dataDir.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (snapshots == null || snapshots.length == 0) {
            log.info("No snapshot files found in {}", dataDir);
            return;
        }
        for (File file : snapshots) {
            String collection = file.getName().replace(".json", "");
            try {
                Map<String, JsonNode> data = objectMapper.readValue(file, MAP_TYPE);
                data.forEach((key, value) -> store.put(collection, key, value));
                log.info("Loaded {} entries into collection '{}' from {}", data.size(), collection, file.getName());
            } catch (IOException e) {
                log.warn("Skipping corrupted snapshot file '{}': {}", file.getName(), e.getMessage());
            }
        }
    }

    public void saveAll() {
        store.getAllCollections().forEach(this::saveCollection);
    }

    private void ensureDataDirExists() {
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create data directory: " + dataDir, e);
        }
    }
}
