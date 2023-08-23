package fr.eaudeparis.syncremocra.job.test;

import fr.eaudeparis.syncremocra.job.Job;
import fr.eaudeparis.syncremocra.repository.pei.PeiRepository;
import fr.eaudeparis.syncremocra.repository.pei.model.ImmutableVuePeiEdpRemocraFilter;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbTestJob implements Job {

  private static Logger logger = LoggerFactory.getLogger(DbTestJob.class);

  private final PeiRepository peiRepository;

  @Inject
  public DbTestJob(PeiRepository peiRepository) {
    this.peiRepository = peiRepository;
  }

  public int run() {
    int count = peiRepository.count(ImmutableVuePeiEdpRemocraFilter.builder().build());
    logger.info("PEI : " + count);
    return 0;
  }
}
