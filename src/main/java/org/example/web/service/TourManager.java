package org.example.web.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                    if (parts.length >= 8) {
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

    public void addTourToFile(LocalDateTime startDateTime, LocalDateTime endDateTime, int duration, int badgeCode, String employeeMail, String userMail) {
        int newId = getLastTourId() + 1;
        String status = "In attesa";
        File file = new File(TOURS_FILE_PATH);

        if (!checkBadgeAvailability(startDateTime, endDateTime)) {
            return;
        }

        try (FileWriter w = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(w)) {

            if (file.length() == 0) {
                bw.write("id;startDateTime;endDateTime;duration;status;badgeCode;employeeMail;userMail\n");
            }
            bw.write(newId + ";" + startDateTime + ";" + endDateTime + ";" + duration + ";" + status + ";" + badgeCode + ";" + employeeMail + ";" + userMail + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int countBookedTours(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int bookedTours = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        try (BufferedReader br = new BufferedReader(new FileReader(TOURS_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("id")) continue;

                String[] columns = line.split(";");
                LocalDateTime start = LocalDateTime.parse(columns[1], formatter);
                LocalDateTime end = LocalDateTime.parse(columns[2], formatter);
                int tourDuration = Integer.parseInt(columns[3]);

                int badgesRequired = (tourDuration == 2) ? 2 : 1;

                if (isOverlapping(start, end, startDateTime, endDateTime)) {
                    bookedTours += badgesRequired;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookedTours;
    }

    private int getAvailableBadges() {
        int availableBadges = 15;
        return availableBadges;
    }

    private boolean checkBadgeAvailability(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int availableBadges = getAvailableBadges();
        int bookedBadges = countBookedTours(startDateTime, endDateTime);

        return bookedBadges < availableBadges;
    }

    private boolean isOverlapping(LocalDateTime existingStart, LocalDateTime existingEnd, LocalDateTime start, LocalDateTime end) {
        boolean isOverlap = existingStart.isBefore(end) && existingEnd.isAfter(start);
        return isOverlap;
    }
}
