package com.avocado.api.store;

import io.dropwizard.core.setup.Environment;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObjectStoreFactoryTest {

    @Mock Environment environment;
    @Mock LifecycleEnvironment lifecycle;
    @Mock SnapshotManager snapshotManager;

    private DataStoreConfiguration config;

    @BeforeEach
    void setUp() {
        when(environment.lifecycle()).thenReturn(lifecycle);

        config = new DataStoreConfiguration();
        config.setSnapshotIntervalSeconds(60);
        config.setSnapshotPath("data/test-snapshot.json");
    }

    @Test
    void initialize_callsLoadAll() throws IOException {
        ObjectStoreFactory.initialize(config, environment, new InMemoryObjectStore(), snapshotManager);

        verify(snapshotManager).loadAll();
    }

    @Test
    void initialize_registersManaged() throws IOException {
        ObjectStoreFactory.initialize(config, environment, new InMemoryObjectStore(), snapshotManager);

        verify(lifecycle).manage(any(Managed.class));
    }

    @Test
    void initialize_returnsTheProvidedStore() throws IOException {
        InMemoryObjectStore store = new InMemoryObjectStore();
        InMemoryObjectStore result = ObjectStoreFactory.initialize(config, environment, store, snapshotManager);

        assertSame(store, result);
    }

    @Test
    void managedStop_callsSaveAll() throws Exception {
        ArgumentCaptor<Managed> managedCaptor = ArgumentCaptor.forClass(Managed.class);
        ObjectStoreFactory.initialize(config, environment, new InMemoryObjectStore(), snapshotManager);
        verify(lifecycle).manage(managedCaptor.capture());

        Managed managed = managedCaptor.getValue();
        managed.start();
        managed.stop();

        verify(snapshotManager, atLeastOnce()).saveAll();
    }

    @Test
    void managedStop_shutsDownExecutorGracefully() throws Exception {
        ArgumentCaptor<Managed> managedCaptor = ArgumentCaptor.forClass(Managed.class);
        ObjectStoreFactory.initialize(config, environment, new InMemoryObjectStore(), snapshotManager);
        verify(lifecycle).manage(managedCaptor.capture());

        Managed managed = managedCaptor.getValue();
        managed.start();

        // stop() must not throw even if saveAll() is a no-op
        assertDoesNotThrow(managed::stop);
    }

    @Test
    void managedStop_whenSaveAllThrows_stillCompletes() throws Exception {
        doThrow(new IOException("disk full")).when(snapshotManager).saveAll();
        ArgumentCaptor<Managed> managedCaptor = ArgumentCaptor.forClass(Managed.class);
        ObjectStoreFactory.initialize(config, environment, new InMemoryObjectStore(), snapshotManager);
        verify(lifecycle).manage(managedCaptor.capture());

        Managed managed = managedCaptor.getValue();
        managed.start();

        // IOException from saveAll propagates from stop() — caller (Dropwizard) handles it
        assertThrows(IOException.class, managed::stop);
    }

    @Test
    void initialize_whenLoadAllThrows_propagatesException() throws IOException {
        doThrow(new IOException("corrupt snapshot")).when(snapshotManager).loadAll();

        assertThrows(IOException.class, () ->
            ObjectStoreFactory.initialize(config, environment, new InMemoryObjectStore(), snapshotManager));
    }
}
