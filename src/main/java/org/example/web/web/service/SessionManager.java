package org.example.web.web.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.NewCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionManager {

    public static final String NOME_COOKIE_SESSION = "Sessione";
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    public NewCookie createUserSession(String email) {
        String idSessione = UUID.randomUUID().toString();
        sessions.put(idSessione, email);

        return new NewCookie.Builder(NOME_COOKIE_SESSION)
                .value(idSessione)
                .path("/")
                .build();
    }

    public String getUserFromSession(String idSessione) {
        return sessions.get(idSessione);
    }

    public void removeUserFromSession(String sessionId) {
        if (sessionId != null) {
            sessions.remove(sessionId);
            log.info("Sessione rimossa per l'ID sessione: {}", sessionId);
        }
    }
}