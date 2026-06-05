package fr.eaudeparis.syncremocra.api;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import org.immutables.value.Value;

@Value.Enclosing
public class ApiModule extends AbstractModule {

  final ApiSettings apiSettings;

  public static ApiModule create(Config config) {
    ImmutableApiSettings.Builder builder = ImmutableApiSettings.builder();
    builder.host(config.getString("host"));
    if (config.hasPath("mail")) {
      builder.mail(config.getString("mail"));
    }
    if (config.hasPath("password")) {
      builder.password(config.getString("password"));
    }
    if (config.hasPath("base_path")) {
      builder.basePath(config.getString("base_path"));
    }
    if (config.hasPath("auth_type")) {
      builder.authType(config.getString("auth_type"));
    }
    if (config.hasPath("keycloak_url")) {
      builder.keycloakUrl(config.getString("keycloak_url"));
    }
    if (config.hasPath("keycloak_realm")) {
      builder.keycloakRealm(config.getString("keycloak_realm"));
    }
    if (config.hasPath("keycloak_client_id")) {
      builder.keycloakClientId(config.getString("keycloak_client_id"));
    }
    if (config.hasPath("keycloak_client_secret")) {
      builder.keycloakClientSecret(config.getString("keycloak_client_secret"));
    }
    return new ApiModule(builder.build());
  }

  public ApiModule(ImmutableApiSettings settings) {
    this.apiSettings = settings;
  }

  @Override
  protected void configure() {
    bind(ApiSettings.class).toInstance(apiSettings);
    bind(ApiEndpoints.class).asEagerSingleton();
  }
}
