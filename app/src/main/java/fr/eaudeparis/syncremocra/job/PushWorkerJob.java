package fr.eaudeparis.syncremocra.job;

import fr.eaudeparis.syncremocra.repository.message.MessageRepository;
import fr.eaudeparis.syncremocra.repository.parametres.ParametresRepository;
import javax.inject.Inject;

public class PushWorkerJob implements Job {

  @Inject MessageRepository messageRepository;

  @Inject ParametresRepository parametresRepository;

  @Override
  public int run() {
    if (this.parametresRepository.get("FLAG_STATUS_PUSHWORKER").equalsIgnoreCase("EN COURS")) {
      this.messageRepository.handlePushWorkerFailure();

    } else {
      this.messageRepository.traiterMessages();
    }

    return 0;
  }
}
