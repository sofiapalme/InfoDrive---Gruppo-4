package org.example.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/userForm")
public class UserFormResource {
    private final Template userForm;

    public UserFormResource(Template userForm) {
        this.userForm = userForm;
    }

    @GET
    public TemplateInstance renderEmployeeLogin() {
        return userForm.instance();
    }
}
