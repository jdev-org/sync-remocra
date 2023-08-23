package fr.eaudeparis.syncremocra.repository.pei.model;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface VuePeiEdpRemocraFilter {

  Optional<String> reference();

  static VuePeiEdpRemocraFilter fromParameters(String reference) {
    ImmutableVuePeiEdpRemocraFilter.Builder builder = ImmutableVuePeiEdpRemocraFilter.builder();
    builder.reference(Optional.ofNullable(reference));

    ImmutableVuePeiEdpRemocraFilter filterForm = builder.build();
    return filterForm;
  }
}
