package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.example.web.web.service.SessionManager;

import java.net.URI;
import java.util.Map;

@Path("/logout")
public class LogoutResource {

    private final SessionManager sessionManager;
    private final Template logout;

    public LogoutResource(SessionManager sessionManager, Template logout) {
        this.sessionManager = sessionManager;
        this.logout = logout;
    }

    @GET
    public TemplateInstance renderLogout(@CookieParam("Sessione") String sessionId) {
        return logout.instance();
    }

    @POST
    public Response logout(@Context HttpHeaders headers) {
        String sessionId = headers.getCookies().get("Sessione") != null ?
                headers.getCookies().get("Sessione").getValue() : null;
        if (sessionId != null) {
            sessionManager.removeUserFromSession(sessionId);
        }

        Response.ResponseBuilder responseBuilder = Response.seeOther(URI.create("/logout"));
        for (Map.Entry<String, Cookie> entry : headers.getCookies().entrySet()) {
            String cookieName = entry.getKey();
            Cookie cookie = entry.getValue();

            NewCookie expiredCookie = new NewCookie(
                    cookieName,
                    null,
                    cookie.getPath(),
                    cookie.getDomain(),
                    "Cookie Expired",
                    0,
                    false,
                    true
            );

            responseBuilder.cookie(expiredCookie);
        }

        return responseBuilder.build();
    }
}
