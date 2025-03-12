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
        String path = Paths.get("files","users.csv").toString();
        File file = new File(path);
        try(FileWriter w = new FileWriter(file,true))
        {
            w.write("\n" + name + ";" + surname + ";" + email);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}