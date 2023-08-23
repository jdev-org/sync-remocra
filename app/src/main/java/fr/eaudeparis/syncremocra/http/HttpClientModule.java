package fr.eaudeparis.syncremocra.http;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.net.http.HttpClient;

public class HttpClientModule extends AbstractModule {
  @Provides
  @Singleton
  HttpClient provideHttpClient() {
    return HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NEVER)
        // TODO: .connectTimeout(), configurabilité
        .build();
  }
}
