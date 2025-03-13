package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    public void loadDataOnFile(
            @FormParam("nome") String name,
            @FormParam("cognome") String surname,
            @FormParam("email") String email
    )
    {
        String path = Paths.get("files", "users.csv").toString();
        File file = new File(path);
        int lastId = 0;

        // Leggere l'ultimo ID dal file
        if (file.exists()) {
            try (var reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length > 0) {
                        try {
                            int id = Integer.parseInt(parts[0]);
                            if (id > lastId) {
                                lastId = id;
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int newId = lastId + 1;

        // Scrivere il nuovo utente nel file
        try (FileWriter w = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(w)) {
            bw.write("\n" + name + ";" + surname + ";" + email + ";" + newId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}