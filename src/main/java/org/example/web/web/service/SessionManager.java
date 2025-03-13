package org.example.web.web.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.NewCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionManager {

    public static final String NOME_COOKIE_SESSION = "Sessione";
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    // Modifica per memorizzare email invece di username
    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    // Crea la sessione e memorizza l'email
    public synchronized NewCookie createUserSession(String email) throws IOException {
        String idSessione = UUID.randomUUID().toString();
        sessions.put(idSessione, email);

        return new NewCookie.Builder(NOME_COOKIE_SESSION)
                .value(idSessione)
                .path("/")
                .build();
    }

    public synchronized String getUserFromSession(String idSessione) {
        return sessions.get(idSessione);
    }


}
