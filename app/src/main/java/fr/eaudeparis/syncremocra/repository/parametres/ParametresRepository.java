package fr.eaudeparis.syncremocra.repository.parametres;

import static fr.eaudeparis.syncremocra.db.model.Tables.PARAMETRES;

import fr.eaudeparis.syncremocra.db.model.tables.Parametres;
import javax.inject.Inject;
import org.jooq.DSLContext;

public class ParametresRepository {

  private final DSLContext context;

  @Inject
  ParametresRepository(DSLContext context) {
    this.context = context;
  }

  public String get(String code) {
    return context
        .select(PARAMETRES.VALEUR)
        .from(PARAMETRES)
        .where(PARAMETRES.CODE.equalIgnoreCase(code))
        .fetchOneInto(String.class);
  }

  public void set(String code, String valeur) {
    context
        .update(Parametres.PARAMETRES)
        .set(Parametres.PARAMETRES.VALEUR, (valeur != null) ? valeur.toUpperCase() : null)
        .where(Parametres.PARAMETRES.CODE.equalIgnoreCase(code))
        .execute();
  }
}
