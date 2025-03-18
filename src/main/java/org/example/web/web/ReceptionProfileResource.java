package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.example.web.service.UserManager;
import org.example.web.web.service.SessionManager;

import java.net.URI;

@Path("/receptionProfile")
public class ReceptionProfileResource {

    private final Template receptionProfile;
    private final UserManager userManager;
    private final SessionManager sessionManager;

    public ReceptionProfileResource(Template receptionProfile, UserManager userManager, SessionManager sessionManager) {
        this.receptionProfile = receptionProfile;
        this.userManager = userManager;
        this.sessionManager = sessionManager;
    }

    @GET
    public TemplateInstance renderReceptionProfile(@CookieParam(SessionManager.NOME_COOKIE_SESSION) String sessionCookie) {

        if (sessionCookie == null) {
            return receptionProfile.data("userName", "Ospite");
        }

        String userEmail = sessionManager.getUserFromSession(sessionCookie);

        if (userEmail == null) {
            return receptionProfile.data("userName", "Ospite");
        }

        String nomeCognome = userManager.getNomeCognomeByEmail(userEmail);

        if (nomeCognome == null) {
            nomeCognome = "Utente Sconosciuto";
        }

        return receptionProfile.data("userName", nomeCognome);
    }

    @GET
    @Path("/redirectToUser")
    public Response redirectToUser() {
        return Response.seeOther(URI.create("/user")).build();
    }

    @GET
    @Path("/redirectToTour")
    public Response redirectToTour() {
        return Response.seeOther(URI.create("/receptionTours")).build();
    }
}

