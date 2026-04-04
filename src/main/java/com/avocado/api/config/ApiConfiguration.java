package com.avocado.api.config;

import com.avocado.api.store.DataStoreConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ApiConfiguration extends Configuration {

    @Valid
    @NotNull
    private DataStoreConfiguration dataStore = new DataStoreConfiguration();

    @JsonProperty("dataStore")
    public DataStoreConfiguration getDataStore() {
        return dataStore;
    }

    @JsonProperty("dataStore")
    public void setDataStore(DataStoreConfiguration dataStore) {
        this.dataStore = dataStore;
    }
}
