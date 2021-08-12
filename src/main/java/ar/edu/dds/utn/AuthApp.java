package ar.edu.dds.utn;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.debug.DebugScreen.enableDebugScreen;

import java.util.*;

import ar.edu.dds.utn.oauth.DemoConfigFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.LogoutRoute;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.mustache.MustacheTemplateEngine;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

/**
 * https://github.com/pac4j/spark-pac4j-demo/
 */
public class AuthApp {

    private final static String JWT_SALT = "12345678901234567890123456789012";

    private final static Logger logger = LoggerFactory.getLogger(AuthApp.class);

    private final static MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();

	public static void main(String[] args) {
		enableDebugScreen();
	    port(4567);

        final Config config = new DemoConfigFactory(JWT_SALT, templateEngine).build();

        get("/", AuthApp::index, templateEngine);
        final CallbackRoute callback = new CallbackRoute(config, null, true);
        //callback.setRenewSession(false);
        get("/callback", callback);
        post("/callback", callback);

        before("/oidc", new SecurityFilter(config, "OidcClient"));
        before("/protected", new SecurityFilter(config, null));

        get("/oidc", AuthApp::protectedIndex, templateEngine);
        get("/protected", AuthApp::protectedIndex, templateEngine);

        final LogoutRoute localLogout = new LogoutRoute(config, "/?defaulturlafterlogout");
        localLogout.setDestroySession(true);
        get("/logout", localLogout);
        final LogoutRoute centralLogout = new LogoutRoute(config);
        centralLogout.setDefaultUrl("http://localhost:8080/?defaulturlafterlogoutafteridp");
        centralLogout.setLogoutUrlPattern("http://localhost:8080/.*");
        centralLogout.setLocalLogout(false);
        centralLogout.setCentralLogout(true);
        centralLogout.setDestroySession(true);
        get("/centralLogout", centralLogout);

        exception(Exception.class, (e, request, response) -> {
            logger.error("Unexpected exception", e);
            response.body(templateEngine.render(new ModelAndView(new HashMap<>(), "error500.mustache")));
        });

	}

    private static ModelAndView index(final Request request, final Response response) {
        final Map map = new HashMap();
        map.put("profiles", getProfiles(request, response));
        final SparkWebContext ctx = new SparkWebContext(request, response);
        map.put("sessionId", ctx.getSessionIdentifier());
        return new ModelAndView(map, "index.mustache");
    }

    private static ModelAndView form(final Config config) {
        final Map map = new HashMap();
        final FormClient formClient = config.getClients().findClient(FormClient.class);
        map.put("callbackUrl", formClient.getCallbackUrl());
        return new ModelAndView(map, "loginForm.mustache");
    }

    private static ModelAndView protectedIndex(final Request request, final Response response) {
        final Map map = new HashMap();
        map.put("profiles", getProfiles(request, response));
        return new ModelAndView(map, "protectedIndex.mustache");
    }

    private static List<CommonProfile> getProfiles(final Request request, final Response response) {
        final SparkWebContext context = new SparkWebContext(request, response);
        final ProfileManager manager = new ProfileManager(context);
        return manager.getAll(true);
    }

    private static ModelAndView forceLogin(final Config config, final Request request, final Response response) {
        final SparkWebContext context = new SparkWebContext(request, response);
        final String clientName = context.getRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER);
        final Client client = config.getClients().findClient(clientName);
        HttpAction action;
        try {
            action = client.redirect(context);
        } catch (final HttpAction e) {
            action = e;
        }
        config.getHttpActionAdapter().adapt(action.getCode(), context);
        return null;
    }
}
