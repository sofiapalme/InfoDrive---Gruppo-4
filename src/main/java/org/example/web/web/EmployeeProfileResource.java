package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import org.example.web.service.UserManager;
import org.example.web.web.service.SessionManager;

@Path("/employeeProfile")
public class EmployeeProfileResource {
    private final Template employeeProfile;
    private final UserManager userManager;
    private final SessionManager sessionManager;

    public EmployeeProfileResource(Template employeeProfile, UserManager userManager, SessionManager sessionManager) {
        this.employeeProfile = employeeProfile;
        this.userManager = userManager;
        this.sessionManager = sessionManager;
    }

    @GET
    public TemplateInstance renderEmployeeProfile(@CookieParam("Sessione") String idSession) {
        sessionManager.checkUserSession(idSession);

        String userEmail = sessionManager.getUserFromSession(idSession);
        String userName = userManager.getNameSurnameByEmail(userEmail);

        return employeeProfile.data("userName", userName);
    }
}




