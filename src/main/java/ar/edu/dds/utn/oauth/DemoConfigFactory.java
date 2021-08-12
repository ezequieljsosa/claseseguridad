package ar.edu.dds.utn.oauth;


import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;

import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;

import spark.TemplateEngine;

public class DemoConfigFactory implements ConfigFactory {

    private final String salt;

    private final TemplateEngine templateEngine;

    public DemoConfigFactory(final String salt, final TemplateEngine templateEngine) {
        this.salt = salt;
        this.templateEngine = templateEngine;
    }

    @Override
    public Config build(final Object... parameters) {
        final OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId("blahblah.apps.googleusercontent.com");
        oidcConfiguration.setSecret("algo muy pero muy secreto");
        oidcConfiguration.setDiscoveryURI("https://accounts.google.com/.well-known/openid-configuration");
        oidcConfiguration.setUseNonce(true);
        //oidcClient.setPreferredJwsAlgorithm(JWSAlgorithm.RS256);
        oidcConfiguration.addCustomParam("prompt", "consent");
        final OidcClient oidcClient = new OidcClient(oidcConfiguration);
        oidcClient.setAuthorizationGenerator((ctx, profile) -> {
            profile.addRole("ROLE_ADMIN");
            return profile;
        });


        final Clients clients = new Clients("http://localhost:4567/callback", oidcClient);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("custom", new CustomAuthorizer());
        config.setHttpActionAdapter(new DemoHttpActionAdapter(templateEngine));
        return config;
    }

}
