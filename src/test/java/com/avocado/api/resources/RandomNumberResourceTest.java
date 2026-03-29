package com.avocado.api.resources;

import com.avocado.api.model.RandomNumber;
import io.dropwizard.testing.junit5.DropwizardTestSupport;
import io.dropwizard.testing.junit5.ResourceTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ResourceTest
public class RandomNumberResourceTest extends DropwizardTestSupport<RandomNumberResource> {

    public RandomNumberResourceTest() {
        super(RandomNumberResource.class);
    }

    @Test
    public void testGetRandomNumber() throws Exception {
        var response = client().target("/random").request().get();

        assertThat(response.getStatus()).isEqualTo(200);
        var randomNumber = response.readEntity(RandomNumber.class);
        assertTrue(randomNumber.number() >= 0.0 && randomNumber.number() <= 1.0);
    }
}
