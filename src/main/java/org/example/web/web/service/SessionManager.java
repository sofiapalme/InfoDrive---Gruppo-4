package org.example.web.web.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionManager {

    public static final String NOME_COOKIE_SESSION = "Sessione";

    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    public NewCookie createUserSession(String email) {
        String idSessione = UUID.randomUUID().toString();
        sessions.put(idSessione, email);

        return new NewCookie.Builder(NOME_COOKIE_SESSION)
                .value(idSessione)
                .path("/")
                .build();
    }

    public void checkUserSession(String idSessione) {
        if (idSessione == null || !sessions.containsKey(idSessione)) {
            throw new WebApplicationException(
                    Response.seeOther(URI.create("/")).build()
            );
        }
    }

    public String getUserFromSession(String idSessione) {
        if (idSessione == null || !sessions.containsKey(idSessione)) {
            throw new WebApplicationException(
                    Response.seeOther(URI.create("/")).build()
            );
        }
        return sessions.get(idSessione);
    }

    public void removeUserFromSession(String sessionId) {
        if (sessionId != null) {
            sessions.remove(sessionId);
        }
    }
}