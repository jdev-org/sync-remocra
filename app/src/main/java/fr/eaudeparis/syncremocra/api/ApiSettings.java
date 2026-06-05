package fr.eaudeparis.syncremocra.api;

import org.immutables.value.Value;

@Value.Immutable
public interface ApiSettings {
  /** @return URL racine de l'instance REMOcRA ou du reverse proxy exposant l'API */
  String host();

  /** @return Préfixe optionnel ajouté devant les endpoints, par exemple {@code /api} */
  @Value.Default
  default String basePath() {
    return "";
  }

  /** @return Type d'authentification API: {@code legacy-jwt} ou {@code keycloak} */
  @Value.Default
  default String authType() {
    return "legacy-jwt";
  }

  /** @return Mot de passe de l'utilisateur API en mode d'authentification historique */
  String password();

  /** @return Adresse e-mail de l'utilisateur API en mode d'authentification historique */
  String mail();

  /** @return URL de base de Keycloak en mode OIDC */
  @Value.Default
  default String keycloakUrl() {
    return "";
  }

  /** @return Realm Keycloak utilisé pour récupérer le jeton OIDC */
  @Value.Default
  default String keycloakRealm() {
    return "";
  }

  /** @return Client ID Keycloak utilisé pour le flux {@code client_credentials} */
  @Value.Default
  default String keycloakClientId() {
    return "";
  }

  /** @return Secret du client Keycloak utilisé pour le flux {@code client_credentials} */
  @Value.Default
  default String keycloakClientSecret() {
    return "";
  }
}
