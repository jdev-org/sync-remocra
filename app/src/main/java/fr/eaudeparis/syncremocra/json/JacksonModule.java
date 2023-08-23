package fr.eaudeparis.syncremocra.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class JacksonModule extends AbstractModule {
  @Provides
  ObjectMapper provideObjectMapper() {
    return new ObjectMapper()
        .registerModule(new GuavaModule())
        .registerModule(new JavaTimeModule())
        .registerModule(new Jdk8Module());
  }
}
