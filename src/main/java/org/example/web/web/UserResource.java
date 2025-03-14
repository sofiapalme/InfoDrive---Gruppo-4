package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.web.service.VisitManager;
import org.example.web.web.service.SessionManager;
import org.jboss.logging.Logger;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Path("/user")
public class UserResource {
    private final Template user;
    private final VisitManager visitManager;
    private final SessionManager sessionManager;

    public UserResource(Template user, VisitManager visitManager, SessionManager sessionManager) {
        this.user = user;
        this.visitManager = visitManager;
        this.sessionManager = sessionManager;
    }

    @GET
    public TemplateInstance renderUser(
            @CookieParam("Sessione")String idSession,
            @QueryParam("message") String message,
            @QueryParam("message_c") String message_c) {

        sessionManager.checkUserSession(idSession);

        return user.data("message", message,
                "message_c", message_c);
    }

    @POST
    public Response loadDataOnFile(
            @FormParam("nome") String name,
            @FormParam("cognome") String surname,
            @FormParam("email") String email
    ) {
        String messaggioErrore = null;
        String messaggioConferma = null;

        if (name == null || name.trim().isEmpty() ||
                surname == null || surname.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
            messaggioErrore = "Tutti i campi sono obbligatori.";
            String encodedMessage = URLEncoder.encode(messaggioErrore, StandardCharsets.UTF_8);
            return Response.seeOther(URI.create("/user?message=" + encodedMessage)).build();
        }

        boolean emailExists = visitManager.emailExists(email);

        if (emailExists) {
            messaggioErrore = "L'user è già stato anagrafato";
            String encodedMessage = URLEncoder.encode(messaggioErrore, StandardCharsets.UTF_8);
            return Response.seeOther(URI.create("/user?message=" + encodedMessage)).build();
        }
        else {
            messaggioConferma = "L'user è stato anagrafato";
            String encodedMessage = URLEncoder.encode(messaggioConferma, StandardCharsets.UTF_8);
            int lastId = visitManager.getLastId();
            visitManager.addUserToFile(name, surname, email, lastId);
            return Response.seeOther(URI.create("/user?message_c=" + encodedMessage)).build();
        }
    }


    @GET
    @Path("/redirectToHome")
    public Response redirectToHome() {
        return Response.seeOther(URI.create("/receptionProfile")).build();
    }
}