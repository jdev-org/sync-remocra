package fr.eaudeparis.syncremocra.job;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeJob implements Job {

  private static Logger logger = LoggerFactory.getLogger(CompositeJob.class);

  private Job[] jobs;

  @Inject
  public CompositeJob(Job... jobs) {
    this.jobs = jobs;
  }

  public int run() {
    logger.info("Jobs start (" + jobs.length + ")");
    try {
      for (Job job : jobs) {
        int result = job.run();
        if (result < 0) {
          logger.info("Job error : " + job.getClass().getCanonicalName());
          return result;
        } else {
          logger.info("Job success : " + job.getClass().getCanonicalName());
        }
      }
      logger.info("Jobs success");
      return 0;
    } catch (Exception e) {
      logger.error("Jobs error", e);
      return -1;
    }
  }
}
