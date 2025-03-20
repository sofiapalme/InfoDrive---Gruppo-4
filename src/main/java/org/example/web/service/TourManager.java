package org.example.web.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public String addTourToFile(LocalDateTime startDateTime, String endDateTime, int duration, Integer badgeCode, String employeeMail, String userMail) {
        if (!isBookingWithMinimumOneDayNotice(startDateTime)) {
            return "Non abbastanza preavviso";
        }

        int newId = getLastTourId() + 1;
        String status = "In attesa";
        File file = new File(TOURS_FILE_PATH);

        try (FileWriter w = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(w)) {

            if (file.length() == 0) {
                bw.write("id;startDateTime;endDateTime;duration;status;badgeCode;employeeMail;userMail\n");
            }
            bw.write(newId + ";" + startDateTime + ";" + endDateTime + ";" + duration + ";" + status + ";" + badgeCode + ";" + employeeMail + ";" + userMail + "\n");
            return "Visita aggiunta correttamente al file";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Errore durante l'aggiunta";
    }

    public String updateBadgeById(String id, String badgeCode)
    {
        if(badgeCode.equals("No badge available"))
        {
            return badgeCode;
        }
        File file = new File(TOURS_FILE_PATH);
        List<String> linesToWrite = new ArrayList<>();
        String header = "id;startDateTime;endDateTime;duration;status;badgeCode;employeeMail;userMail";
        linesToWrite.add(header);
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                if(splittedLine[0].equals(id)&& splittedLine[4].equals("Terminata"))
                {
                    return "Visita già terminata";
                }
                else if(splittedLine[0].equals(id) && splittedLine[4].equals("In corso"))
                {
                    return "Visita già in corso";
                }
                else if(splittedLine[0].equals(id))
                {
                    linesToWrite.add(splittedLine[0] + ";" +
                            splittedLine[1] + ";" +
                            splittedLine[2] + ";" +
                            splittedLine[3] + ";" +
                            "In corso" + ";" +
                            badgeCode + ";" +
                            splittedLine[6] + ";" +
                            splittedLine[7] + ";"
                    );
                }
                else
                {
                    linesToWrite.add(line);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try(BufferedWriter br = new BufferedWriter(new FileWriter(file)))
        {
            for(String line : linesToWrite)
            {
                br.write(line);
                br.newLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "Badge assegnato correttamente";
    }

    public String freeBadgeById(String id){
        File file = new File(TOURS_FILE_PATH);
        String badgeToFree = null;
        List<String> linesToWrite = new ArrayList<>();
        String header = "id;startDateTime;endDateTime;duration;status;badgeCode;employeeMail;userMail";
        linesToWrite.add(header);
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                if(splittedLine[0].equals(id) && Objects.equals(splittedLine[4], "Terminata"))
                {
                    return "Visita già terminata";
                }
                if(splittedLine[0].equals(id) && !Objects.equals(splittedLine[5], "0"))
                {
                    badgeToFree = splittedLine[5];
                    linesToWrite.add(splittedLine[0] + ";" +
                            splittedLine[1] + ";" +
                            LocalDateTime.parse(splittedLine[1]).plusHours(Long.valueOf(splittedLine[3])) + ";" +
                            splittedLine[3] + ";" +
                            "Terminata" + ";" +
                            0 + ";" +
                            splittedLine[6] + ";" +
                            splittedLine[7] + ";"
                    );
                }
                else
                {
                    linesToWrite.add(line);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(badgeToFree == null)
        {
            return "Visita non ancora incominciata";
        }
        try(BufferedWriter br = new BufferedWriter(new FileWriter(file)))
        {
            for(String line : linesToWrite)
            {
                br.write(line);
                br.newLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return badgeToFree;
    }

    public String getBadgeToFree(String id)
    {
        File file = new File(TOURS_FILE_PATH);
        String badgeToFree = null;
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                if(splittedLine[0].equals(id) && !Objects.equals(splittedLine[5], "0"))
                {
                    badgeToFree = splittedLine[5];
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return badgeToFree;
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

    public boolean isBookingWithMinimumOneDayNotice(LocalDateTime startDateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minimumNoticeTime = startDateTime.minusDays(1);

        return now.isBefore(minimumNoticeTime);
    }
}