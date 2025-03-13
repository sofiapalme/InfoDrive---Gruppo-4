package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.web.service.VisitManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import static java.nio.file.Paths.get;
import static spark.Spark.port;

@Path("/user")
public class UserResource {
    private final Template user;
    private VisitManager visitManager;

    public UserResource(Template user, VisitManager visitManager) {
        this.user = user;
        this.visitManager = visitManager;
    }

    @GET
    public TemplateInstance renderUser(){
        return user.instance();
    }

    @POST
    public Response loadDataOnFile(
            @FormParam("nome") String name,
            @FormParam("cognome") String surname,
            @FormParam("email") String email
    ) {
        if (name == null || name.trim().isEmpty() ||
            surname == null || surname.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Tutti i campi sono obbligatori.")
                    .build();
        }

        int lastId = visitManager.getLastId();

        visitManager.addUserToFile(name,surname,email,lastId);

        return Response.seeOther(URI.create("/user?success=true")).build();
    }

    @GET
    @Path("/redirectToHome")
    public Response redirectToHome() {
        return Response.seeOther(URI.create("/receptionProfile")).build();
    }
}