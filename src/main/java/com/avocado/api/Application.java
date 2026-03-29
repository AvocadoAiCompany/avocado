package com.avocado.api;

import com.avocado.api.resource.GreetingResource;
import com.avocado.api.service.SystemTimeService;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class Application extends Application<com.avocado.api.config.ApiConfiguration> {
    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public void run(com.avocado.api.config.ApiConfiguration configuration, Environment environment) {
        environment.jersey().register(new GreetingResource(new SystemTimeService()));
    }
}
