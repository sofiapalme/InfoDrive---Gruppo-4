package org.example.web.web.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.NewCookie;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SessionManager {

    public static final String NOME_COOKIE_SESSION = "Sessione";

    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    public NewCookie createUserSession(String username) throws IOException {
        String idSessione = UUID.randomUUID().toString();
        sessions.put(idSessione, username);
        return new NewCookie.Builder(NOME_COOKIE_SESSION).value(idSessione).build();
    }
}
