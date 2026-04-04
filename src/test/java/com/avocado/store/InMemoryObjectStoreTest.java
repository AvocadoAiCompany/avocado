package com.avocado.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryObjectStoreTest {

    private InMemoryObjectStore<String, String> store;

    @BeforeEach
    void setUp() {
        store = new InMemoryObjectStore<>();
    }

    // --- put / get ---

    @Test
    void get_returnsEmpty_whenKeyAbsent() {
        assertEquals(Optional.empty(), store.get("missing"));
    }

    @Test
    void get_returnsValue_afterPut() {
        store.put("k", "v");
        assertEquals(Optional.of("v"), store.get("k"));
    }

    @Test
    void put_replacesExistingValue() {
        store.put("k", "first");
        store.put("k", "second");
        assertEquals(Optional.of("second"), store.get("k"));
    }

    @Test
    void put_throwsOnNullKey() {
        assertThrows(NullPointerException.class, () -> store.put(null, "v"));
    }

    @Test
    void put_throwsOnNullValue() {
        assertThrows(NullPointerException.class, () -> store.put("k", null));
    }

    @Test
    void get_throwsOnNullKey() {
        assertThrows(NullPointerException.class, () -> store.get(null));
    }

    // --- delete ---

    @Test
    void delete_returnsFalse_whenKeyAbsent() {
        assertFalse(store.delete("missing"));
    }

    @Test
    void delete_returnsTrue_andRemovesEntry() {
        store.put("k", "v");
        assertTrue(store.delete("k"));
        assertEquals(Optional.empty(), store.get("k"));
    }

    @Test
    void delete_throwsOnNullKey() {
        assertThrows(NullPointerException.class, () -> store.delete(null));
    }

    // --- contains ---

    @Test
    void contains_returnsFalse_whenKeyAbsent() {
        assertFalse(store.contains("missing"));
    }

    @Test
    void contains_returnsTrue_afterPut() {
        store.put("k", "v");
        assertTrue(store.contains("k"));
    }

    @Test
    void contains_returnsFalse_afterDelete() {
        store.put("k", "v");
        store.delete("k");
        assertFalse(store.contains("k"));
    }

    @Test
    void contains_throwsOnNullKey() {
        assertThrows(NullPointerException.class, () -> store.contains(null));
    }

    // --- values ---

    @Test
    void values_returnsEmpty_onNewStore() {
        assertTrue(store.values().isEmpty());
    }

    @Test
    void values_containsAllStoredValues() {
        store.put("a", "1");
        store.put("b", "2");
        var vals = store.values();
        assertEquals(2, vals.size());
        assertTrue(vals.contains("1"));
        assertTrue(vals.contains("2"));
    }

    @Test
    void values_isUnmodifiable() {
        store.put("k", "v");
        var vals = store.values();
        assertThrows(UnsupportedOperationException.class, () -> vals.add("x"));
    }

    // --- size ---

    @Test
    void size_isZero_onNewStore() {
        assertEquals(0, store.size());
    }

    @Test
    void size_incrementsOnPut() {
        store.put("a", "1");
        store.put("b", "2");
        assertEquals(2, store.size());
    }

    @Test
    void size_doesNotChange_onPutWithExistingKey() {
        store.put("k", "first");
        store.put("k", "second");
        assertEquals(1, store.size());
    }

    @Test
    void size_decrementsOnDelete() {
        store.put("k", "v");
        store.delete("k");
        assertEquals(0, store.size());
    }
}
