package com.avocado.api.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SnapshotManager {

    private static final TypeReference<Map<String, byte[]>> MAP_TYPE = new TypeReference<>() {};

    private final InMemoryObjectStore store;
    private final Path snapshotPath;
    private final ObjectMapper mapper;

    public SnapshotManager(InMemoryObjectStore store, Path snapshotPath) {
        this(store, snapshotPath, new ObjectMapper());
    }

    SnapshotManager(InMemoryObjectStore store, Path snapshotPath, ObjectMapper mapper) {
        this.store = store;
        this.snapshotPath = snapshotPath;
        this.mapper = mapper;
    }

    public void loadAll() throws IOException {
        if (!Files.exists(snapshotPath)) {
            return;
        }
        Map<String, byte[]> snapshot = mapper.readValue(snapshotPath.toFile(), MAP_TYPE);
        store.restore(snapshot);
    }

    public void saveAll() throws IOException {
        Path parent = snapshotPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        mapper.writeValue(snapshotPath.toFile(), store.snapshot());
    }
}
