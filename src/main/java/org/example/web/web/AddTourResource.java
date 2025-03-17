package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.example.web.service.TourManager;
import org.example.web.service.VisitManager;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Path("/addTour")
public class AddTourResource {
    private final Template addTour;
    private final VisitManager visitManager;
    private final TourManager tourManager;

    public AddTourResource(Template addTour, VisitManager visitManager, TourManager tourManager) {
        this.addTour = addTour;
        this.visitManager = visitManager;
        this.tourManager = tourManager;
    }

    @GET
    public TemplateInstance renderAddVisit() {
        return addTour.instance();
    }

    @POST
    public Response processAddVisit(
            @FormParam("name") String name,
            @FormParam("surname") String surname,
            @FormParam("email") String email,
            @FormParam("startDateTime")String startDateTimeString,
            @FormParam("endDateTime") String endDateTimeString,
            @FormParam("estimatedDuration")String estimatedDurationString
    ) {
        System.out.println(name);
        System.out.println(surname);
        System.out.println(email);

        int userId = visitManager.checkUserExistence(name, surname, email);

        System.out.println("user id:" + userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeString, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString, formatter);

        System.out.println("Inizio: " + startDateTime);
        System.out.println("Fine: " + endDateTime);

        int estimatedDuration = Integer.parseInt(estimatedDurationString);

        System.out.println("Durata in ore: " + estimatedDuration);

        int lastId = tourManager.getLastTourId();

        System.out.println("Ultimo id visita: " + lastId);

        int employeeFk = 0;

        tourManager.addTourToFile(startDateTime,endDateTime, employeeFk, userId);

        return Response.seeOther(URI.create("/addTour")).build();
    }
}
