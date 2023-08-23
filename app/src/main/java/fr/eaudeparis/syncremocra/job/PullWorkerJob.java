package fr.eaudeparis.syncremocra.job;

import fr.eaudeparis.syncremocra.repository.parametres.ParametresRepository;
import fr.eaudeparis.syncremocra.repository.pullmessage.PullMessageRepository;
import javax.inject.Inject;

public class PullWorkerJob implements Job {

  @Inject PullMessageRepository pullMessageRepository;

  @Inject ParametresRepository parametresRepository;

  @Override
  public int run() {

    if (this.parametresRepository.get("FLAG_STATUS_PULLWORKER").equalsIgnoreCase("EN COURS")) {
      this.pullMessageRepository.handlePushWorkerFailure();

    } else {
      this.pullMessageRepository.recupererInformations();
    }

    return 0;
  }
}
