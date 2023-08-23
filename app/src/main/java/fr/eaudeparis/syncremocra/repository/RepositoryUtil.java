package fr.eaudeparis.syncremocra.repository;

import java.util.List;
import java.util.Optional;
import org.jooq.Condition;
import org.jooq.Field;

public class RepositoryUtil {
  public static <T> void addIfPresent(
      Optional<T> value, Field<T> field, List<Condition> conditions) {
    if (value.isPresent()) {
      conditions.add(field.eq(value.get()));
    }
  }
}
