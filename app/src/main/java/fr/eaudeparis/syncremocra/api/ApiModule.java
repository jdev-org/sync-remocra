package fr.eaudeparis.syncremocra.api;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import org.immutables.value.Value;

@Value.Enclosing
public class ApiModule extends AbstractModule {

  final ApiSettings apiSettings;

  public static ApiModule create(Config config) {
    ImmutableApiSettings.Builder builder = ImmutableApiSettings.builder();
    builder
        .host(config.getString("host"))
        .mail(config.getString("mail"))
        .password(config.getString("password"));
    return new ApiModule(builder.build());
  }

  public ApiModule(ImmutableApiSettings settings) {
    this.apiSettings = settings;
  }

  @Override
  protected void configure() {
    bind(ApiSettings.class).toInstance(apiSettings);
  }
}
