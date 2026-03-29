package com.avocado.api;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import jakarta.ws.rs.core.MediaType;

public class Application extends Application<Configuration> {
    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(RandomNumberResource.class);
    }

    public static void main(String[] args) {
        new Application().run(args);
    }
}
