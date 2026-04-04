package com.avocado.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryObjectStoreTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private InMemoryObjectStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryObjectStore();
    }

    // --- put / get ---

    @Test
    void put_and_get_returnsStoredValue() {
        JsonNode node = MAPPER.valueToTree(Map.of("name", "avocado"));
        store.put("fruits", "1", node);

        Optional<JsonNode> result = store.get("fruits", "1");

        assertTrue(result.isPresent());
        assertEquals(node, result.get());
    }

    @Test
    void get_missingCollection_returnsEmpty() {
        assertTrue(store.get("nonexistent", "key").isEmpty());
    }

    @Test
    void get_missingKey_returnsEmpty() {
        store.put("fruits", "1", MAPPER.valueToTree("apple"));
        assertTrue(store.get("fruits", "missing").isEmpty());
    }

    @Test
    void put_overwritesExistingKey() {
        store.put("col", "k", MAPPER.valueToTree("old"));
        store.put("col", "k", MAPPER.valueToTree("new"));

        assertEquals("new", store.get("col", "k").orElseThrow().asText());
    }

    // --- delete ---

    @Test
    void delete_removesKey() {
        store.put("col", "k", MAPPER.valueToTree("v"));
        store.delete("col", "k");

        assertTrue(store.get("col", "k").isEmpty());
    }

    @Test
    void delete_missingKey_doesNotThrow() {
        assertDoesNotThrow(() -> store.delete("col", "ghost"));
    }

    @Test
    void delete_missingCollection_doesNotThrow() {
        assertDoesNotThrow(() -> store.delete("nonexistent", "k"));
    }

    // --- getAll ---

    @Test
    void getAll_missingCollection_returnsEmptyMap() {
        Map<String, JsonNode> result = store.getAll("none");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_returnsAllEntries() {
        store.put("col", "a", MAPPER.valueToTree(1));
        store.put("col", "b", MAPPER.valueToTree(2));

        Map<String, JsonNode> result = store.getAll("col");

        assertEquals(2, result.size());
        assertEquals(1, result.get("a").asInt());
        assertEquals(2, result.get("b").asInt());
    }

    @Test
    void getAll_returnsDefensiveCopy() {
        store.put("col", "x", MAPPER.valueToTree("val"));

        Map<String, JsonNode> snapshot = store.getAll("col");
        snapshot.put("injected", MAPPER.valueToTree("should-not-appear"));

        assertFalse(store.getAll("col").containsKey("injected"));
    }

    // --- edge cases: special characters in collection/key names ---

    @Test
    void put_and_get_collectionWithSpecialCharacters() {
        String collection = "my collection/2024 🥑 #test";
        JsonNode node = MAPPER.valueToTree("value");
        store.put(collection, "k", node);

        assertEquals(node, store.get(collection, "k").orElseThrow());
    }

    @Test
    void put_and_get_keyWithSpecialCharacters() {
        JsonNode node = MAPPER.valueToTree(42);
        String key = "key with spaces & emojis 🎉\nnewline\ttab";
        store.put("col", key, node);

        assertEquals(node, store.get("col", key).orElseThrow());
    }

    @Test
    void put_and_get_veryLongCollectionAndKey() {
        String longName = "x".repeat(10_000);
        JsonNode node = MAPPER.valueToTree(true);
        store.put(longName, longName, node);

        assertEquals(node, store.get(longName, longName).orElseThrow());
    }

    // --- concurrent access ---

    @Test
    void concurrentPuts_noDataCorruption() throws InterruptedException {
        int threads = 20;
        int itemsPerThread = 500;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            int threadId = t;
            pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < itemsPerThread; i++) {
                        String key = "t" + threadId + "-i" + i;
                        store.put("shared", key, MAPPER.valueToTree(threadId * itemsPerThread + i));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(10, TimeUnit.SECONDS));
        pool.shutdown();

        Map<String, JsonNode> all = store.getAll("shared");
        assertEquals(threads * itemsPerThread, all.size());
    }

    @Test
    void concurrentPutsAndGets_noExceptions() throws InterruptedException {
        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        List<Throwable> errors = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            int threadId = t;
            pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < 200; i++) {
                        String key = "k" + i;
                        if (threadId % 2 == 0) {
                            store.put("col", key, MAPPER.valueToTree(i));
                        } else {
                            store.get("col", key);
                        }
                    }
                } catch (Exception | Error e) {
                    synchronized (errors) { errors.add(e); }
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(10, TimeUnit.SECONDS));
        pool.shutdown();

        assertTrue(errors.isEmpty(), "Unexpected errors: " + errors);
    }

    @Test
    void concurrentDeletesAndGets_noExceptions() throws InterruptedException {
        for (int i = 0; i < 500; i++) {
            store.put("col", "k" + i, MAPPER.valueToTree(i));
        }

        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        List<Throwable> errors = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            int threadId = t;
            pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < 500; i++) {
                        if (threadId % 2 == 0) {
                            store.delete("col", "k" + i);
                        } else {
                            store.get("col", "k" + i);
                        }
                    }
                } catch (Exception | Error e) {
                    synchronized (errors) { errors.add(e); }
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(10, TimeUnit.SECONDS));
        pool.shutdown();

        assertTrue(errors.isEmpty(), "Unexpected errors: " + errors);
    }
}
