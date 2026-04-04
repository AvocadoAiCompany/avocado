package com.avocado.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DataStoreConfigurationTest {

    private final ObjectMapper mapper = Jackson.newObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private DataStoreConfiguration config;

    @BeforeEach
    void setUp() {
        config = new DataStoreConfiguration();
    }

    @Test
    void defaultDataDirIsSet() {
        assertThat(config.getDataDir()).isEqualTo("./data");
    }

    @Test
    void defaultSnapshotIntervalSecondsIsSet() {
        assertThat(config.getSnapshotIntervalSeconds()).isEqualTo(60);
    }

    @Test
    void validatesSuccessfullyWithDefaults() {
        Set<ConstraintViolation<DataStoreConfiguration>> violations = validator.validate(config);
        assertThat(violations).isEmpty();
    }

    @Test
    void rejectsBlankDataDir() {
        config.setDataDir("");
        Set<ConstraintViolation<DataStoreConfiguration>> violations = validator.validate(config);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("dataDir");
    }

    @Test
    void rejectsZeroSnapshotInterval() {
        config.setSnapshotIntervalSeconds(0);
        Set<ConstraintViolation<DataStoreConfiguration>> violations = validator.validate(config);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("snapshotIntervalSeconds");
    }

    @Test
    void deserializesFromJson() throws Exception {
        String json = """
                {"dataDir": "/var/data", "snapshotIntervalSeconds": 120}
                """;
        DataStoreConfiguration result = mapper.readValue(json, DataStoreConfiguration.class);
        assertThat(result.getDataDir()).isEqualTo("/var/data");
        assertThat(result.getSnapshotIntervalSeconds()).isEqualTo(120);
    }

    @Test
    void serializesToJson() throws Exception {
        config.setDataDir("/var/data");
        config.setSnapshotIntervalSeconds(30);
        String json = mapper.writeValueAsString(config);
        assertThat(json).contains("\"dataDir\":\"/var/data\"");
        assertThat(json).contains("\"snapshotIntervalSeconds\":30");
    }
}
