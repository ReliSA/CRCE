package org.valesz.restest;

import org.valesz.restest.model.Pet;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("pet")
    public Pet getPet() {
        return new Pet();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("special/pet/{longParam}")
    public Pet getSpecialPet(@PathParam("longParam") Number longParam, @QueryParam("stringParam") String stringParam) {
        return new Pet();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public List<Pet> getAll() {
        return Arrays.asList(
                new Pet(),
                new Pet(),
                new Pet()
        );
    }
}
