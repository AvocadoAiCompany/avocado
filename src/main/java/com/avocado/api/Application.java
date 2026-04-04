package com.avocado.api;

import com.avocado.api.config.ApiConfiguration;
import com.avocado.api.resource.GreetingResource;
import com.avocado.api.service.CountryTimezoneResolver;
import com.avocado.api.service.SystemTimeService;
import com.avocado.api.store.ObjectStoreFactory;
import io.dropwizard.core.setup.Environment;

public class Application extends io.dropwizard.core.Application<ApiConfiguration> {

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public void run(ApiConfiguration configuration, Environment environment) throws Exception {
        ObjectStoreFactory.build(configuration.getDataStore(), environment);

        environment.jersey().register(
            new GreetingResource(new SystemTimeService(), new CountryTimezoneResolver())
        );
    }
}
