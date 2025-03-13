package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.example.web.service.UserManager;
import org.example.web.web.service.SessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

@Path("/receptionProfile")
public class ReceptionProfileResource {

    private static final Logger log = LoggerFactory.getLogger(ReceptionProfileResource.class);
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
        log.info("Cookie della sessione ricevuto: " + sessionCookie);

        if (sessionCookie == null) {
            log.warn("Sessione non trovata!");
            return receptionProfile.data("userName", "Ospite");
        }

        String userEmail = sessionManager.getUserFromSession(sessionCookie);
        log.info("Email recuperata dalla sessione: " + userEmail);

        if (userEmail == null) {
            log.warn("Email non trovata nella sessione!");
            return receptionProfile.data("userName", "Ospite");
        }

        String nomeCognome = userManager.getNomeCognomeByEmail(userEmail);
        log.info("Nome e cognome recuperati: " + nomeCognome);

        if (nomeCognome == null) {
            log.warn("Nome e Cognome non trovati per l'email: " + userEmail);
            nomeCognome = "Utente Sconosciuto";
        }

        return receptionProfile.data("userName", nomeCognome);
    }

    @GET
    @Path("/redirectToUser")
    public Response redirectToUser() {
        return Response.seeOther(URI.create("/user")).build();
    }

}

