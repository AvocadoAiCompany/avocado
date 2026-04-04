package com.avocado.api.store;

import io.dropwizard.core.setup.Environment;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ObjectStoreFactory {

    private static final Logger log = LoggerFactory.getLogger(ObjectStoreFactory.class);

    private ObjectStoreFactory() {}

    public static InMemoryObjectStore build(DataStoreConfiguration config, Environment environment)
            throws IOException {
        InMemoryObjectStore store = new InMemoryObjectStore();
        SnapshotManager snapshotManager = new SnapshotManager(store, Path.of(config.getSnapshotPath()));
        return initialize(config, environment, store, snapshotManager);
    }

    /** Package-private entry point allowing injection of components for testing. */
    static InMemoryObjectStore initialize(DataStoreConfiguration config, Environment environment,
            InMemoryObjectStore store, SnapshotManager snapshotManager) throws IOException {

        snapshotManager.loadAll();

        environment.lifecycle().manage(new Managed() {
            private ScheduledExecutorService executor;

            @Override
            public void start() {
                executor = Executors.newSingleThreadScheduledExecutor(
                        r -> new Thread(r, "snapshot-scheduler"));
                long interval = config.getSnapshotIntervalSeconds();
                // scheduleWithFixedDelay waits for each save to finish before starting
                // the next delay, preventing task pile-up if a save runs long.
                executor.scheduleWithFixedDelay(
                        () -> safeSnapshot(snapshotManager),
                        interval, interval, TimeUnit.SECONDS);
                log.info("Snapshot scheduler started (interval={}s)", interval);
            }

            @Override
            public void stop() throws Exception {
                snapshotManager.saveAll();
                executor.shutdown();
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("Snapshot executor did not terminate within 30s");
                    executor.shutdownNow();
                }
                log.info("Snapshot scheduler stopped");
            }
        });

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> safeSnapshot(snapshotManager), "snapshot-shutdown-hook"));

        return store;
    }

    private static void safeSnapshot(SnapshotManager snapshotManager) {
        try {
            snapshotManager.saveAll();
        } catch (IOException e) {
            log.error("Failed to save snapshot", e);
        }
    }
}
