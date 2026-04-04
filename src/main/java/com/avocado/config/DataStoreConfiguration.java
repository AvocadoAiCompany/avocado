package com.avocado.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class DataStoreConfiguration {

    @NotEmpty
    private String dataDir = "./data";

    @Min(1)
    private int snapshotIntervalSeconds = 60;

    @JsonProperty
    public String getDataDir() {
        return dataDir;
    }

    @JsonProperty
    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
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
