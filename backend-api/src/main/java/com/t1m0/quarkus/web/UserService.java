package com.t1m0.quarkus.web;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.t1m0.quarkus.web.model.User;

@Path("/users")
public interface UserService {

    @POST
    User save(User user);

    @PUT
    @Path("/{uuid}")
    User save(@PathParam("uuid") String uuid, User user);

    @GET
    @Path("/{uuid}")
    User find(@PathParam("uuid") String uuid);

    @GET
    Page<User> findAll(@QueryParam("limit") @DefaultValue("100") int limit, @QueryParam("page") @DefaultValue("0") int page);
}
