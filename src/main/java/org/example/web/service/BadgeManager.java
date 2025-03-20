package org.example.web.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BadgeManager {
    private static final String BADGE_FILE_PATH = Paths.get("files", "badge.csv").toString();

    public String getFirstBadge() throws IOException {
        File file = new File(BADGE_FILE_PATH);
        List<String> linesToWrite = new ArrayList<>();
        String header = "code;status";
        linesToWrite.add(header);
        String code = null;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                if(splittedLine[1].equals("available") && code == null)
                {
                    code = splittedLine[0];
                    linesToWrite.add(splittedLine[0] + ";unavailable");
                }
                else
                {
                    linesToWrite.add(line);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if(code == null)
        {
            return "Nessun badge disponibile";
        }
        try(BufferedWriter br = new BufferedWriter(new FileWriter(file)))
        {
            for(String line : linesToWrite)
            {
                br.write(line);
                br.newLine();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public void freeBadge(String badgeCode) throws IOException {
        File file = new File(BADGE_FILE_PATH);
        List<String> linesToWrite = new ArrayList<>();
        String header = "code;status";
        linesToWrite.add(header);
        boolean found = false;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                if(splittedLine[0].equals(badgeCode) && !found)
                {
                    linesToWrite.add(splittedLine[0] + ";available");
                    found = true;
                }
                else
                {
                    linesToWrite.add(line);
                }
            }
        }
        catch (Exception e) {
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAvailableBadgesCount() throws IOException {
        File file = new File(BADGE_FILE_PATH);
        int count = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                if("available".equals(splittedLine[0]))
                {
                    count++;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(count);
    }
}