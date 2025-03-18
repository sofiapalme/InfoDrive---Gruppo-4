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
            @FormParam("estimatedDuration")String durationString,
            @FormParam("employeeEmail") String employeeEmail
    ) {
        String userMail = visitManager.checkUserExistence(name, surname, email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeString, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString, formatter);

        int duration = Integer.parseInt(durationString);

        int lastId = tourManager.getLastTourId();

        tourManager.addTourToFile(startDateTime,endDateTime, duration,  55555, employeeEmail, userMail);

        return Response.seeOther(URI.create("/addTour")).build();
    }
}
