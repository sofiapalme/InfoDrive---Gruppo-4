package org.example.web.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.example.web.web.LoginResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

@ApplicationScoped
public class UserManager {

    private static final Logger log = LoggerFactory.getLogger(LoginResource.class);

    public String loginCheckPassword(String inputEmail, String inputPassword) {
        String filePath = Paths.get("files", "dipendente.csv").toString();
        File f = new File(filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(";");

                if (credentials.length >= 5) {
                    String email = credentials[2].trim();
                    String password = credentials[3].trim();
                    String role = credentials[4].trim();


                    if (email.equals(inputEmail) && password.equals(inputPassword)) {
                        if(role.equals("portineria")) {
                            return "portineria";
                        }
                        else
                        {
                            return "dipendente";
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getNomeCognomeByEmail(String emailInput) {
        String filePath = Paths.get("files", "dipendente.csv").toString();
        File f = new File(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] dati = line.split(";");

                if (dati.length >= 5) {
                    String email = dati[2].trim();
                    String nome = dati[0].trim();
                    String cognome = dati[1].trim();

                    if (email.equals(emailInput)) {
                        return nome + " " + cognome;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
