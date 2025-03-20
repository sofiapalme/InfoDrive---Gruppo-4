package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.web.Tour;
import org.example.web.service.BadgeManager;
import org.example.web.service.TourManager;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Path("/receptionTours")
public class ReceptionToursResource {
    private final Template receptionTours;
    private final BadgeManager badgeManager;
    private final TourManager tourManager;
    private List<Tour> tourList;

    public ReceptionToursResource(Template receptionTours, BadgeManager badgeManager, TourManager tourManager) {
        this.receptionTours = receptionTours;
        this.badgeManager = badgeManager;
        this.tourManager = tourManager;
        this.tourList = getToursFromFile();
    }

    @GET
    public TemplateInstance renderReceptionTours() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        tourList.sort(Comparator.comparing(tour -> LocalDateTime.parse(tour.getStartDateTime(),formatter)));
        return receptionTours.data("message",null).data("tourList",tourList);
    }

    @GET
    @Path("/redirectToHome")
    public Response redirectToHome() {
        return Response.seeOther(URI.create("/receptionProfile")).build();
    }

    @POST
    @Path("/getFilteredTours")
    public TemplateInstance getFilteredTours(@FormParam("date") String date)
    {
        List<Tour> filteredTours = new ArrayList<>();
        tourList.forEach(tour -> {
            if(tour.getStartDateTime().split("T")[0].equals(date))
            {
                filteredTours.add(tour);
            }
        });
        if(filteredTours.size() > 0)
        {
            return receptionTours.data("message",null).data("tourList",filteredTours);
        }
        return receptionTours.data("message","Nessuna visita trovata per questa data").data("tourList",null);
    }

    @POST
    @Path("/removeTour")
    public TemplateInstance removeTour(@QueryParam("tourId") int tourId) throws IOException {

        removeTourById(tourId);
        tourList = getToursFromFile();
        if(!tourList.isEmpty())
        {
            return receptionTours.data("message",null).data("tourList",tourList);
        }
        return receptionTours.data("message","Nessuna visita presente, prenotane di nuove").data("tourList",tourList);
    }

    @POST
    @Path("/addBadge")
    public TemplateInstance addBadge(@QueryParam("tourId") int tourId) throws IOException {
        String badge = badgeManager.getFirstBadge();
        if ("No badge available".equals(badge)) {
            return receptionTours.data("message", badge).data("tourList", tourList);
        }

        String result = tourManager.updateBadgeById(String.valueOf(tourId), badge);
        tourList = getToursFromFile();

        if ("Badge assegnato correttamente".equals(result)) {
            return receptionTours.data("message", null).data("tourList", tourList);
        } else {
            // Se il tour è già terminato o in corso, rilascia il badge appena assegnato
            badgeManager.freeBadge(badge);
            return receptionTours.data("message", result).data("tourList", tourList);
        }
    }

    @POST
    @Path("/endTour")
    public TemplateInstance endTour(@QueryParam("tourId") int tourId) throws IOException {
        String badgeToFree = tourManager.freeBadgeById(String.valueOf(tourId));
        if(("Visita non ancora incominciata").equals(badgeToFree) || ("Visita già terminata").equals(badgeToFree))
        {
            tourList = getToursFromFile();
            return receptionTours.data("message",badgeToFree).data("tourList",tourList);
        }
        else
        {
            tourList = getToursFromFile();
            badgeManager.freeBadge(badgeToFree);
            return receptionTours.data("message",null).data("tourList",tourList);
        }
    }

    @POST
    @Path("/showAll")
    public TemplateInstance showAllTours()
    {
        return receptionTours.data("message",null).data("tourList",tourList);
    }


    private String getNameAndSurname(String email, String file)
    {
        String path = Paths.get("files",file).toString();
        File f = new File(path);
        try(BufferedReader br = new BufferedReader(new FileReader(f)))
        {
            String line;
            br.readLine();
            while((line = br.readLine()) != null)
            {
                String[] splittedLine = line.split(";");
                if(splittedLine[2].equals(email))
                {
                    return splittedLine[0] + " " + splittedLine[1];
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private List<Tour> getToursFromFile()
    {
        List<Tour> tourList = new ArrayList<>();
        String path = Paths.get("files","tours.csv").toString();
        File f = new File(path);

        try(BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            String[] splittedLine;
            br.readLine();
            while ((line = br.readLine()) != null)
            {
                splittedLine = line.split(";");
                Tour tour = new Tour(
                        Integer.parseInt(splittedLine[0]),
                        splittedLine[1],
                        splittedLine[2],
                        Integer.parseInt(splittedLine[3]),
                        splittedLine[4],
                        Integer.parseInt(splittedLine[5]),
                        getNameAndSurname(splittedLine[6],"dipendente.csv"),
                        getNameAndSurname(splittedLine[7],"users.csv")
                );
                tourList.add(tour);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tourList;
    }

    private void removeTourById(int tourId) throws IOException {
        String path = Paths.get("files", "tours.csv").toString();
        List<String> linesToKeep = new ArrayList<>();
        String header = "id;startDateTime;endDateTime;duration;status;badgeCode;employeeMail;userMail";
        linesToKeep.add(header);
        //LEGGE IL FILE E SI TIENE LE RIGHE DA NON ELIMINARE
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
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for(String line : linesToKeep)
            {
                bw.write(line + "\n");
            }
        }
    }

}
