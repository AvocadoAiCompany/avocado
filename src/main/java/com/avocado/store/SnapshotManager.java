package com.avocado.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class SnapshotManager {

    private static final Logger LOG = LoggerFactory.getLogger(SnapshotManager.class);
    private static final TypeReference<Map<String, JsonNode>> MAP_TYPE = new TypeReference<>() {};

    private final Path dataDir;
    private final InMemoryObjectStore store;
    private final ObjectMapper mapper;

    public SnapshotManager(String dataDir, InMemoryObjectStore store, ObjectMapper mapper) throws IOException {
        this.dataDir = Path.of(dataDir);
        this.store = store;
        this.mapper = mapper;
        Files.createDirectories(this.dataDir);
    }

    public void saveCollection(String collection) throws IOException {
        Map<String, JsonNode> entries = store.getAll(collection);
        Path target = dataDir.resolve(collection + ".json");
        Path temp = dataDir.resolve(collection + ".json.tmp");
        mapper.writeValue(temp.toFile(), entries);
        Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    public void saveAll() throws IOException {
        for (String collection : store.collections()) {
            saveCollection(collection);
        }
    }

    public void loadAll() {
        try (var stream = Files.newDirectoryStream(dataDir, "*.json")) {
            for (Path file : stream) {
                String filename = file.getFileName().toString();
                String collection = filename.substring(0, filename.length() - ".json".length());
                try {
                    Map<String, JsonNode> entries = mapper.readValue(file.toFile(), MAP_TYPE);
                    entries.forEach((key, value) -> store.put(collection, key, value));
                } catch (IOException | RuntimeException e) {
                    LOG.warn("Corrupted snapshot for collection '{}', skipping", collection, e);
                }
            }
        } catch (IOException e) {
            LOG.warn("Failed to scan snapshot directory '{}', skipping loadAll", dataDir, e);
        }
    }
}
