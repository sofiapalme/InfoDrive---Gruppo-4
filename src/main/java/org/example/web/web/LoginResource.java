package org.example.web.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.example.web.service.UserManager;
import org.example.web.web.service.SessionManager;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

@Path("/")
public class LoginResource {

    private static final Logger log = LoggerFactory.getLogger(LoginResource.class);
    private final Template login;
    private final UserManager utentiManager;
    private final SessionManager sessionManager;

    public LoginResource(Template login, UserManager userManager) {
        this.login = login;
        this.utentiManager = userManager;
        this.sessionManager = new SessionManager();
    }

    @GET
    public TemplateInstance mostraPaginaLogin() {
        return login.data("message", null);
    }

    @POST
    public Response processaLogin(
            @FormParam("email") String email,
            @FormParam("password") String password
    ) throws IOException {
        String messaggioErrore = null;
        String result = utentiManager.loginCheckPassword(email, password);
        if (result == null) {
            messaggioErrore = "Email o password non validi";
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(login.data("message", messaggioErrore))
                    .build();
        }

        NewCookie sessionCookie = sessionManager.createUserSession(email);
        if(result.equals("portineria"))
        {
            return Response
                    .seeOther(URI.create("/portineriaProfile"))
                    .cookie(sessionCookie)
                    .build();
        }
        else
        {
            return Response
                    .seeOther(URI.create("/employeeProfile"))
                    .cookie(sessionCookie)
                    .build();
        }
    }
}
