package com.avocado.api.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryObjectStoreTest {

    private InMemoryObjectStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryObjectStore();
    }

    @Test
    void putAndGet_returnsStoredValue() {
        store.put("key1", new byte[]{1, 2, 3});
        assertTrue(store.get("key1").isPresent());
        assertArrayEquals(new byte[]{1, 2, 3}, store.get("key1").get());
    }

    @Test
    void get_missingKey_returnsEmpty() {
        assertTrue(store.get("missing").isEmpty());
    }

    @Test
    void delete_removesKey() {
        store.put("key1", new byte[]{1});
        store.delete("key1");
        assertTrue(store.get("key1").isEmpty());
    }

    @Test
    void keys_reflectsCurrentEntries() {
        store.put("a", new byte[]{});
        store.put("b", new byte[]{});
        assertTrue(store.keys().containsAll(java.util.Set.of("a", "b")));
    }

    @Test
    void snapshot_capturesCurrentState() {
        store.put("x", new byte[]{9});
        Map<String, byte[]> snap = store.snapshot();
        assertEquals(1, snap.size());
        assertArrayEquals(new byte[]{9}, snap.get("x"));
    }

    @Test
    void restore_replacesAllData() {
        store.put("old", new byte[]{1});
        store.restore(Map.of("new", new byte[]{2}));
        assertTrue(store.get("old").isEmpty());
        assertArrayEquals(new byte[]{2}, store.get("new").get());
    }
}
