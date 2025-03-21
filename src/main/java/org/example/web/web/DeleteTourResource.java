package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.web.Tour;
import org.example.web.web.service.SessionManager;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.quarkus.arc.ComponentsProvider.LOG;

@Path("/deleteTour")
public class DeleteTourResource {
    private final Template deleteTour;
    private List<Tour> tourList;
    private final SessionManager sessionManager;

    public DeleteTourResource(Template deleteTour, SessionManager sessionManager) {
        this.deleteTour = deleteTour;
        this.tourList = getToursFromFile();
        this.sessionManager = sessionManager;
    }

    @GET
    public TemplateInstance renderReceptionTours(@CookieParam("Sessione") String idSession) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        sessionManager.checkUserSession(idSession);

        String userEmail = sessionManager.getUserFromSession(idSession);

        List<Tour> filteredTours = tourList.stream()
                .filter(tour -> {
                    LOG.infof("Email della visita: %s", tour.getEmployeeFk());
                    return tour.getEmployeeFk().equals(userEmail);
                })
                .collect(Collectors.toList());

        filteredTours.sort(Comparator.comparing(tour -> LocalDateTime.parse(tour.getStartDateTime(), formatter)));

        return deleteTour.data("message", null).data("tourList", filteredTours);
    }

    @POST
    @Path("/getFilteredTours")
    public TemplateInstance getFilteredTours(@FormParam("date") String date, @CookieParam("Sessione") String idSession) {
        sessionManager.checkUserSession(idSession);
        String userEmail = sessionManager.getUserFromSession(idSession);

        List<Tour> filteredTours = new ArrayList<>();
        tourList.forEach(tour -> {
            if (tour.getStartDateTime().split("T")[0].equals(date) && tour.getEmployeeFk().equals(userEmail)) {
                filteredTours.add(tour);
            }
        });

        if (filteredTours.size() > 0) {
            return deleteTour.data("message", null).data("tourList", filteredTours);
        }
        return deleteTour.data("message", "Nessuna visita trovata per questa data").data("tourList", null);
    }

    @POST
    @Path("/removeTour")
    public TemplateInstance removeTour(@QueryParam("tourId") int tourId, @CookieParam("Sessione") String idSession) throws IOException {
        sessionManager.checkUserSession(idSession);

        removeTourById(tourId);
        tourList = getToursFromFile();

        String userEmail = sessionManager.getUserFromSession(idSession);
        List<Tour> filteredTours = tourList.stream()
                .filter(tour -> tour.getEmployeeFk().equals(userEmail))
                .collect(Collectors.toList());

        if (!filteredTours.isEmpty()) {
            return deleteTour.data("message", null).data("tourList", filteredTours);
        }
        return deleteTour.data("message", "Nessuna visita presente, prenotane di nuove").data("tourList", filteredTours);
    }

    @POST
    @Path("/showAll")
    public TemplateInstance showAllTours(@CookieParam("Sessione") String idSession) {
        sessionManager.checkUserSession(idSession);
        String userEmail = sessionManager.getUserFromSession(idSession);

        List<Tour> filteredTours = tourList.stream()
                .filter(tour -> tour.getEmployeeFk().equals(userEmail))
                .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        filteredTours.sort(Comparator.comparing(tour -> LocalDateTime.parse(tour.getStartDateTime(), formatter)));
        return deleteTour.data("message", null).data("tourList", filteredTours);
    }

    private String getNameAndSurname(String email, String file) {
        String path = Paths.get("files", file).toString();
        File f = new File(path);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                if (splittedLine[2].equals(email)) {
                    return splittedLine[0] + " " + splittedLine[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Tour> getToursFromFile() {
        List<Tour> tourList = new ArrayList<>();
        String path = Paths.get("files", "tours.csv").toString();
        File f = new File(path);

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            String[] splittedLine;
            br.readLine();
            while ((line = br.readLine()) != null) {
                splittedLine = line.split(";");
                String userMail = splittedLine[7].trim();
                String userFk = getNameAndSurname(userMail, "users.csv");

                if (userFk == null) {
                    userFk = userMail;
                }

                Tour tour = new Tour(
                        Integer.parseInt(splittedLine[0]),
                        splittedLine[1],
                        splittedLine[2],
                        Integer.parseInt(splittedLine[3]),
                        splittedLine[4],
                        Integer.parseInt(splittedLine[5]),
                        splittedLine[6],
                        userFk
                );
                tourList.add(tour);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tourList;
    }

    private void removeTourById(int tourId) throws IOException {
        String path = Paths.get("files", "tours.csv").toString();
        List<String> linesToKeep = new ArrayList<>();
        String header = "id;startDateTime;endDateTime;duration;status;badgeCode;employeeMail;userMail";
        linesToKeep.add(header);

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (!(line.split(";")[0].equals(tourId + ""))) {
                    linesToKeep.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String line : linesToKeep) {
                bw.write(line + "\n");
            }
        }
    }

    @GET
    @Path("/redirectToHome")
    public Response redirectToHome() {
        return Response.seeOther(URI.create("/employeeProfile")).build();
    }
}