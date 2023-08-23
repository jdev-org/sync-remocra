package fr.eaudeparis.syncremocra.api;

import org.immutables.value.Value;

@Value.Immutable
public interface ApiSettings {
  String host();

  String password();

  String mail();
}
