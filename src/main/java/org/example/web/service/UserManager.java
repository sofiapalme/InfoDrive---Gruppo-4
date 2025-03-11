package org.example.web.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@ApplicationScoped
public class UserManager {

    public boolean loginCheckPassword(String inputEmail, String inputPassword) {
        try (BufferedReader br = new BufferedReader(new FileReader("csv/dipendente.csv"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(";");

                if (credentials.length >= 6) {
                    String email = credentials[2].trim();
                    String password = credentials[3].trim();

                    if (email.equals(inputEmail) && password.equals(inputPassword)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }
}
