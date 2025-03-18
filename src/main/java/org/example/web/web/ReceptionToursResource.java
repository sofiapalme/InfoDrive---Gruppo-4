package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.example.web.Tour;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    public ReceptionToursResource(Template receptionTours) {
        this.receptionTours = receptionTours;
    }

    @GET
    public TemplateInstance renderReceptionTours() {
        List<Tour> tourList = getToursFromFile();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        tourList.sort(Comparator.comparing(tour -> LocalDateTime.parse(tour.getStartDateTime(),formatter)));
        return receptionTours.data("message",null).data("tourList",tourList);
    }

    @POST
    public TemplateInstance getFilteredTours(@FormParam("date") String date)
    {
        List<Tour> tourList = getToursFromFile();
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
}
