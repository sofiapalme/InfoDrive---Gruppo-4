package org.example.web.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@ApplicationScoped
public class TourManager {
    private static final String TOURS_FILE_PATH = Paths.get("files", "tours.csv").toString();

    public int getLastTourId() {
        int lastId = 0;
        File file = new File(TOURS_FILE_PATH);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length >= 5) {
                        try {
                            int id = Integer.parseInt(parts[0]);
                            if (id > lastId) {
                                lastId = id;
                            }
                        } catch (NumberFormatException ignored) {
                            ignored.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lastId;
    }

    public void addTourToFile(LocalDateTime startDateTime, LocalDateTime endDateTime, int duration, int badgeCode, int employeeFk, int userFk) {
        int newId = getLastTourId() + 1;
        String status = "In attesa";
        File file = new File(TOURS_FILE_PATH);

        try (FileWriter w = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(w)) {

            if (file.length() == 0) {
                bw.write("Id;StartDateTime;EndDateTime;Duration;Status;EmployeeFk;UserFk\n");
            }
            bw.write(newId + ";" + startDateTime + ";" + endDateTime + ";" + duration + ";" + status + ";" + badgeCode + ";" + employeeFk + ";" + userFk + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


