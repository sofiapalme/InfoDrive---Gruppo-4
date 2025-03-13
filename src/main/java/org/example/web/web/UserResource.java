package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

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

    public UserResource(Template user) {
        this.user = user;
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

        String path = Paths.get("files", "users.csv").toString();
        File file = new File(path);
        int lastId = 0;

        file.getParentFile().mkdirs();

        if (file.exists()) {
            try (var reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length > 3) {
                        try {
                            int id = Integer.parseInt(parts[3]);
                            if (id > lastId) {
                                lastId = id;
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int newId = lastId + 1;

        try (FileWriter w = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(w)) {
            if (file.length() == 0) {
                bw.write("Nome;Cognome;Email;ID\n");
            }
            bw.write(name + ";" + surname + ";" + email + ";" + newId + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response.seeOther(URI.create("/user?success=true")).build();
    }

    @GET
    @Path("/redirectToHome")
    public Response redirectToHome() {
        return Response.seeOther(URI.create("/receptionProfile")).build();
    }
}