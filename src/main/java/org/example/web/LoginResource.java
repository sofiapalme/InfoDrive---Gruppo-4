package org.example.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class LoginResource {
    private final Template employeeLogin;

    public LoginResource(Template employeeLogin) {
        this.employeeLogin = employeeLogin;
    }

    @GET
    public TemplateInstance renderEmployeeLogin() {
        return employeeLogin.instance();
    }
}
