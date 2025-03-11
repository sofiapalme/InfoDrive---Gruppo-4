package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/employeeProfile")
public class EmployeeProfileResource {
    private final Template employeeProfile;

    public EmployeeProfileResource(Template employeeProfile) {
        this.employeeProfile = employeeProfile;
    }

    @GET
    public TemplateInstance renderEmployeeProfile() {
        return employeeProfile.instance();
    }
}
