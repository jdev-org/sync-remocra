package fr.eaudeparis.syncremocra.repository.pullmessage.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

/**
 * Modèle local des modifications PEI.
 *
 * <p>Cette classe absorbe à la fois le contrat historique et le contrat REMOcRA v3 afin de
 * conserver des accesseurs stables dans le reste du code pendant la migration.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeiDiffModel {

  @JsonAlias({"numero", "numeroComplet"})
  private String numero;

  @JsonAlias({"dateModification", "momentModification"})
  private Date dateModification;

  private String utilisateurModification;

  private String utilisateurModificationOrganisme;

  @JsonAlias({"organismeModification", "auteurModification"})
  private String organismeModification;

  private String auteurModificationFlag;

  @JsonAlias({"operation", "typeOperation"})
  private String operation;

  @JsonAlias({"type", "typeObjet"})
  private String type;

  private Auteur auteur;

  public String getNumero() {
    return numero;
  }

  public void setNumero(String numero) {
    this.numero = numero;
  }

  public Date getDateModification() {
    return dateModification;
  }

  public void setDateModification(Date dateModification) {
    this.dateModification = dateModification;
  }

  public String getUtilisateurModification() {
    return utilisateurModification != null ? utilisateurModification : organismeModification;
  }

  public void setUtilisateurModification(String utilisateurModification) {
    this.utilisateurModification = utilisateurModification;
  }

  public String getUtilisateurModificationOrganisme() {
    return utilisateurModificationOrganisme;
  }

  public void setUtilisateurModificationOrganisme(String utilisateurModificationOrganisme) {
    this.utilisateurModificationOrganisme = utilisateurModificationOrganisme;
  }

  public String getOrganismeModification() {
    return organismeModification;
  }

  public void setOrganismeModification(String organismeModification) {
    this.organismeModification = organismeModification;
  }

  public String getAuteurModificationFlag() {
    if (auteurModificationFlag != null) {
      return auteurModificationFlag;
    }
    if (auteur == null || auteur.typeSourceModification == null) {
      return null;
    }
    if ("API".equalsIgnoreCase(auteur.typeSourceModification)) {
      return "API";
    }
    return "USER";
  }

  public void setAuteurModificationFlag(String auteurModificationFlag) {
    this.auteurModificationFlag = auteurModificationFlag;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getType() {
    if ("PEI".equalsIgnoreCase(type)) {
      return "CARACTERISTIQUES";
    }
    if ("VISITE".equalsIgnoreCase(type)) {
      return "VISITES";
    }
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Auteur getAuteur() {
    return auteur;
  }

  public void setAuteur(Auteur auteur) {
    this.auteur = auteur;
  }

  /**
   * Indique si la modification provient de l'organisme courant et doit donc être ignorée côté pull.
   *
   * <p>Avec le contrat v3, l'organisme d'un auteur web/mobile n'est plus disponible dans le diff.
   * Dans ce cas, on reste conservateur et on considère la modification comme externe.
   *
   * @param nomOrganisme Organisme courant côté sync
   * @return {@code true} si la modification doit être considérée comme locale
   */
  public boolean isModifiedByCurrentOrganisme(String nomOrganisme) {
    String auteurFlag = getAuteurModificationFlag();
    if (auteurFlag == null || nomOrganisme == null) {
      return false;
    }
    if ("API".equals(auteurFlag)) {
      return nomOrganisme.equals(getOrganismeModification());
    }
    if ("USER".equals(auteurFlag) || "ETL".equals(auteurFlag)) {
      return nomOrganisme.equals(getUtilisateurModificationOrganisme());
    }
    return false;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Auteur {
    @JsonAlias("typeSourceModification")
    private String typeSourceModification;

    public String getTypeSourceModification() {
      return typeSourceModification;
    }

    public void setTypeSourceModification(String typeSourceModification) {
      this.typeSourceModification = typeSourceModification;
    }
  }
}
