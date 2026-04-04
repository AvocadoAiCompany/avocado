package com.avocado.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public class DataStoreConfiguration {

    @NotEmpty
    @JsonProperty
    private String dataDir = "data";

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
}
