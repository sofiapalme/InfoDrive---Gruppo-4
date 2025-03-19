package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.web.service.TourManager;
import org.example.web.service.VisitManager;
import org.example.web.web.service.SessionManager;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Path("/addTour")
public class AddTourResource {
    private final Template addTour;
    private final VisitManager visitManager;
    private final TourManager tourManager;
    private final SessionManager sessionManager;

    public AddTourResource(Template addTour, VisitManager visitManager, TourManager tourManager, SessionManager sessionManager) {
        this.addTour = addTour;
        this.visitManager = visitManager;
        this.tourManager = tourManager;
        this.sessionManager = sessionManager;
    }

    @GET
    public TemplateInstance renderAddVisit() {
        return addTour.data("message",null);
    }

    @POST
    public TemplateInstance processAddVisit(
            @FormParam("name") String name,
            @FormParam("surname") String surname,
            @FormParam("email") String email,
            @FormParam("startDateTime") String startDateTimeString,
            @FormParam("estimatedDuration") String durationString,
            @CookieParam(SessionManager.NOME_COOKIE_SESSION) String sessionCookie
    ) {
        String userMail = visitManager.checkUserExistence(name, surname, email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeString, formatter);
        LocalDateTime endDateTime = startDateTime.plusHours(durationString.equals("1") ? 1 : 2);

        int duration = Integer.parseInt(durationString);

        String response = tourManager.addTourToFile(startDateTime, endDateTime, duration, 55555, sessionManager.getUserFromSession(sessionCookie), userMail);

        return switch (response) {
            case "Non abbastanza preavviso" ->
                    addTour.data("message", "Non hai dato abbastanza prevviso per prenotare la visita");
            case "Visita aggiunta correttamente al file" ->
                    addTour.data("message", "Visita aggiunta correttamente");
            case "Errore durante l'aggiunta" ->
                    addTour.data("message", "Errore durante l'aggiunta");
            default ->
                    addTour.data("message", "Case non previsto");
        };
    }
}