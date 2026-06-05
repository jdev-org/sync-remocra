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

  /** @return Type d'authentification API supporté par le client v3, soit {@code keycloak} */
  @Value.Default
  default String authType() {
    return "keycloak";
  }

  /** @return Mot de passe historique conservé pour compatibilité de configuration locale */
  @Value.Default
  default String password() {
    return "";
  }

  /** @return Adresse e-mail historique conservée pour compatibilité de configuration locale */
  @Value.Default
  default String mail() {
    return "";
  }

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
