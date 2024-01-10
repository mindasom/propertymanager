package min.propertymonitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.http.HttpClient;
import java.util.logging.Logger;

@Component
public class MonitorScheduler {
    private Logger logger = Logger.getLogger(MonitorScheduler.class.getName());

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

//    @Scheduled(cron = "0 0/5 * * * *")
    @Scheduled(fixedDelay = 300000)
    public void runMonitor() {
        String monitoringDirectory = PropertymonitorApplication.monitorDirectory;

        logger.info("Started monitoring " + monitoringDirectory);

        File directoryToMonitorFile = new File(monitoringDirectory);

        for (File propertyFile : directoryToMonitorFile.listFiles()) {
            taskExecutor.execute(new PropertyFileMonitor(
                    propertyFile.getAbsolutePath(),
                    PropertymonitorApplication.keyFilter,
                    PropertymonitorApplication.serverUrl,
                    HttpClient.newHttpClient()));
        }

        taskExecutor.shutdown();
    }
}
