package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.example.web.Tour;
import org.example.web.service.TourManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/receptionTours")
public class ReceptionToursResource {
    private final Template receptionTours;
    private static final Logger log = LoggerFactory.getLogger(ReceptionProfileResource.class);

    public ReceptionToursResource(Template receptionTours) {
        this.receptionTours = receptionTours;
    }

    @GET
    public TemplateInstance renderReceptionTours() {
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
                log.info(Arrays.toString(splittedLine));
                Tour tour = new Tour(
                        Integer.parseInt(splittedLine[0]),
                        splittedLine[1],
                        splittedLine[2],
                        Integer.parseInt(splittedLine[3]),
                        splittedLine[4],
                        0,
                        Integer.parseInt(splittedLine[6])
                );
                tourList.add(tour);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return receptionTours.data("tourList",tourList);
    }
}
