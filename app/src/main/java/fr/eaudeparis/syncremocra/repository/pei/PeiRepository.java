package fr.eaudeparis.syncremocra.repository.pei;

import static fr.eaudeparis.syncremocra.db.model.Tables.VUE_PEI_EDP_REMOCRA;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.eaudeparis.syncremocra.db.model.tables.pojos.VuePeiEdpRemocra;
import fr.eaudeparis.syncremocra.repository.RepositoryUtil;
import fr.eaudeparis.syncremocra.repository.model.SortOrder;
import fr.eaudeparis.syncremocra.repository.pei.model.VuePeiEdpRemocraFilter;
import fr.eaudeparis.syncremocra.repository.pei.model.VuePeiEdpRemocraSort;
import fr.eaudeparis.syncremocra.util.APIAuthentException;
import fr.eaudeparis.syncremocra.util.APIConnectionException;
import fr.eaudeparis.syncremocra.util.JSONUtil;
import fr.eaudeparis.syncremocra.util.RequestException;
import fr.eaudeparis.syncremocra.util.RequestManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.immutables.value.Value;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectField;
import org.jooq.SelectWhereStep;
import org.jooq.SortField;

@Value.Enclosing
public class PeiRepository {

  private final DSLContext context;

  @Inject
  PeiRepository(DSLContext context) {
    this.context = context;
  }

  @Inject RequestManager requestManager;

  public int count(VuePeiEdpRemocraFilter filters) {
    return context.fetchCount(getSelect().where(getFiltersCondition(filters)).getQuery());
  }

  public List<VuePeiEdpRemocra> read(
      int offset,
      int limit,
      VuePeiEdpRemocraFilter filters,
      VuePeiEdpRemocraSort sortField,
      SortOrder sortOrder) {
    SelectConditionStep<Record> query = getSelect().where(getFiltersCondition(filters));
    if (sortField != null) {
      query.orderBy(getSortField(sortField, sortOrder));
    }
    query.limit(offset, limit);
    return query.fetchInto(VuePeiEdpRemocra.class);
  }

  private SelectWhereStep<Record> getSelect(SelectField<?>... fields) {
    return context.select(fields).from(VUE_PEI_EDP_REMOCRA);
  }

  private List<? extends Condition> getFiltersCondition(VuePeiEdpRemocraFilter filters) {

    List<Condition> res = new ArrayList<>();
    if (filters == null) {
      return res;
    }
    if (filters.reference().isPresent()) {
      RepositoryUtil.addIfPresent(filters.reference(), VUE_PEI_EDP_REMOCRA.REFERENCE, res);
    }
    return res;
  }

  private SortField<?> getSortField(VuePeiEdpRemocraSort sortField, SortOrder sortOrder) {
    Field<?> field;
    switch (sortField) {
      case reference:
        field = VUE_PEI_EDP_REMOCRA.REFERENCE;
        break;
      default:
        throw new IllegalArgumentException("Invalid sort field");
    }
    return (sortOrder == SortOrder.asc ? field.asc() : field.desc()).nullsLast();
  }

  /**
   * Renvoie les anomalies qu'il est possible de contrôler et constater sur un PEI via le croisement
   * des référentiels de l'API Ces anomalies sont déterminées selon le type de PEI ainsi que sa
   * nature, et par la configuration qui a été réalisée sur REMOcRA
   *
   * @param reference La référence du PEI
   * @param contexte Le contexte de visite (code du type de saisie)
   * @param bloquante Seulement les anomalies bloquantes ou non
   * @return Un arraylist contenant les codes des anomalies qui sont accessibles
   * @throws RequestException Une erreur a été renvoyée par l'API
   * @throws JsonProcessingException Une erreur est survenue lors de la lecture des informations
   *     retournées par l'API
   * @throws APIConnectionException Inpossible de contacter l'API
   * @throws APIAuthentException Impossible de s'authentifier sur l'API
   */
  public ArrayList<String> getNaturesAnomaliesAccessibles(
      String reference, String contexte, boolean bloquante)
      throws RequestException, JsonProcessingException, APIConnectionException,
          APIAuthentException {
    String dataPei = this.requestManager.sendGetRequest("/api/deci/pei/" + reference);

    ObjectMapper mapper = new ObjectMapper();
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
    Map<String, Object> data = mapper.readValue(dataPei, typeRef);

    String type = null;
    String nature = JSONUtil.getString(data, "nature");
    if ("PIBI".equalsIgnoreCase(JSONUtil.getString(data, "type"))) {
      type = "pibi";
    } else if ("PENA".equalsIgnoreCase(JSONUtil.getString(data, "type"))) {
      type = "pena";
    }

    String path =
        "/api/deci/referentiel/"
            + type
            + "/"
            + nature
            + "/naturesAnomalies?contexteVisite="
            + contexte;
    String dataAnomalies = this.requestManager.sendGetRequest(path);
    ArrayList<String> anomalies = new ArrayList<String>();

    TypeReference<ArrayList<Map<String, Object>>> typeRefAnomalies =
        new TypeReference<ArrayList<Map<String, Object>>>() {};
    for (Map<String, Object> anomalie : mapper.readValue(dataAnomalies, typeRefAnomalies)) {
      String code = JSONUtil.getString(anomalie, "code");
      Integer valIndispo = JSONUtil.getInteger(anomalie, "valIndispoTerrestre");
      // Si le paramatetre bloquante et a true
      // on ne renvoi que les anomalies BLOQUANTE (valindispo ==5)
      if ((code != null && code.length() > 0 && !anomalies.contains(code))
          && bloquante
          && Integer.valueOf(5).equals(valIndispo)) {
        anomalies.add(code);
        // Sinon on renvois TOUT les anomalies accessible pour tel contexte de visite
      } else if ((code != null && code.length() > 0 && !anomalies.contains(code)) && !bloquante) {
        anomalies.add(code);
      }
    }

    return anomalies;
  }
}
