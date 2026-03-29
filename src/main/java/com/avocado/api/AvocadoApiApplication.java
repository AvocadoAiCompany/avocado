package com.avocado.api;

import com.avocado.api.resources.HelloWorldResource;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class AvocadoApiApplication extends Application<AvocadoApiConfiguration> {

    public static void main(String[] args) throws Exception {
        new AvocadoApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "avocado-api";
    }

    @Override
    public void initialize(Bootstrap<AvocadoApiConfiguration> bootstrap) {
    }

    @Override
    public void run(AvocadoApiConfiguration config, Environment environment) {
        environment.jersey().register(new HelloWorldResource());
    }
}
