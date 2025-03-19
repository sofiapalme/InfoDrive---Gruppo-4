package org.example.web.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.*;
import java.nio.file.Paths;

@ApplicationScoped
public class VisitManager {
    private static final String USERS_FILE_PATH = Paths.get("files", "users.csv").toString();

    public boolean emailExists(String email) {
        File file = new File(USERS_FILE_PATH);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 3 && parts[2].trim().equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserToFile(String name, String surname, String email) {
        if (emailExists(email)) {
            return;
        }
        File file = new File(USERS_FILE_PATH);
        try (FileWriter w = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(w)) {

            if (file.length() == 0) {
                bw.write("Nome;Cognome;Email;ID\n");
            }
            bw.write(name + ";" + surname + ";" + email + ";");
            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String checkUserExistence(String name, String surname, String email) {
        File usersFile = new File(USERS_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] dati = line.split(";");
                if (dati.length >= 3) {
                    String n = dati[0].trim();
                    String s = dati[1].trim();
                    String em = dati[2].trim();

                    if (n.equals(name) && s.equals(surname) && em.equals(email)) {
                        return dati[2].trim();
                    }
                }
            }

            addUserToFile(name, surname, email);
            return email;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}