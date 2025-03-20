package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.example.web.Tour;

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
    private List<Tour> tourList;

    public ReceptionToursResource(Template receptionTours) {
        this.receptionTours = receptionTours;
        this.tourList = getToursFromFile();
    }

    @GET
    public TemplateInstance renderReceptionTours() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        tourList.sort(Comparator.comparing(tour -> LocalDateTime.parse(tour.getStartDateTime(),formatter)));
        return receptionTours.data("message",null).data("tourList",tourList);
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
    @Path("/showAll")
    public TemplateInstance showAllTours()
    {
        return receptionTours.data("message",null).data("tourList",tourList);
    }

    @POST
    @Path("/addBadge")
    public TemplateInstance addBadge(@QueryParam("tourId") int tourId) throws IOException {
        if(updateBadgeById(tourId).equals("No badge available"))
        {
            return receptionTours.data("message","Nessun badge disponibile").data("tourList",tourList);
        }
        tourList = getToursFromFile();
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

    private String updateBadgeById(int id) throws IOException {
        String path = Paths.get("files", "tours.csv").toString();
        String header = "id;startDateTime;endDateTime;duration;badgeCode;employeeMail;userMail";
        List<String> updatedLines = new ArrayList<>();
        updatedLines.add(header);
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                if((line.split(";")[0].equals(id+""))) {
                    if(getFirstBadge().equals("Nessun badge disponible"))
                    {
                        return "No badge available";
                    }
                    String[] splittedLine = line.split(";");
                    String newLine =
                            splittedLine[0] + ";"
                            + splittedLine[1] + ";"
                            + splittedLine[2] + ";"
                            + splittedLine[3] + ";"
                            + "In corso" + ";"
                            + getFirstBadge() + ";"
                            + splittedLine[6] + ";"
                            + splittedLine[7] + ";";
                    updatedLines.add(newLine);
                }
                else
                {
                    updatedLines.add(line);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for(String line : updatedLines)
            {
                bw.write(line + "\n");
            }
        }
        return "File updated";
    }

    private String getFirstBadge() throws FileNotFoundException {
        boolean found = false;
        String path = Paths.get("files", "badge.csv").toString();
        File f = new File(path);
        List<String> updatedLines = new ArrayList<>();
        String header = "badgeCode;status";
        updatedLines.add(header);
        String code = null;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                if (!found && splittedLine.length == 2 && splittedLine[1].equals("true")) {
                    code = splittedLine[0];
                    updatedLines.add(code + ";" + "false"); // Disattiva il badge
                    found = true;
                } else {
                    updatedLines.add(line); // Mantiene gli altri badge inalterati
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (code == null) {
            return "Nessun badge disponibile"; // Nessun badge trovato
        }

        // Scrivi solo se c'è stato un aggiornamento
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String updatedLine : updatedLines) {
                bw.write(updatedLine);
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return code; // Ritorna il badge assegnato
    }
    @GET
    @Path("/redirectToHome")
    public Response redirectToHome() {
        return Response.seeOther(URI.create("/receptionProfile")).build();
    }
}


