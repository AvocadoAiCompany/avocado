package com.avocado.api.store;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataStoreConfiguration {

    private String snapshotPath = "data/store-snapshot.json";
    private int snapshotIntervalSeconds = 300;

    @JsonProperty
    public String getSnapshotPath() {
        return snapshotPath;
    }

    @JsonProperty
    public void setSnapshotPath(String snapshotPath) {
        this.snapshotPath = snapshotPath;
    }

    @JsonProperty
    public int getSnapshotIntervalSeconds() {
        return snapshotIntervalSeconds;
    }

    @JsonProperty
    public void setSnapshotIntervalSeconds(int snapshotIntervalSeconds) {
        this.snapshotIntervalSeconds = snapshotIntervalSeconds;
    }
}
