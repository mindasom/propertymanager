package min.propertymonitor;

import min.propertyhelpers.ConfigFileHelper;
import min.propertymonitor.helpers.ConfigurationHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

@EnableScheduling
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PropertymonitorApplication {
	public static final String CONFIG_DIRECTORY_TO_MONITOR = "monitor.dir";

	public static final String DEFAULT_DIRECTORY_TO_MONITOR = "properties";

	public static final String CONFIG_KEY_FILTER = "key.filter";

	public static final String DEFAULT_KEY_FILTER = ".+";

	public static final String CONFIG_SERVER_URL = "server.url";

	public static final String DEFAULT_SERVER_URL = "http://localhost:8080/properties/send";

	public static String monitorDirectory;
	public static String keyFilter;

	public static String serverUrl;

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(2);
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
//		taskExecutor.setAwaitTerminationSeconds(240);
		taskExecutor.initialize();
		return taskExecutor;
	}

	public static void main(String[] args) {
		Properties properties = new Properties();

		if (args != null && args.length > 0) {
			ConfigFileHelper.loadPropertiesFromConfigFile(properties, args[0]);
		}

		validateConfiguration(properties);

		SpringApplication propertyMonitorApp = new SpringApplication(PropertymonitorApplication.class);

		setConfigurationValues(properties, propertyMonitorApp);

		SpringApplication.run(PropertymonitorApplication.class, args);
	}

	private static void setConfigurationValues(Properties properties, SpringApplication propertyMonitorApp) {
		monitorDirectory = properties.getProperty(CONFIG_DIRECTORY_TO_MONITOR, DEFAULT_DIRECTORY_TO_MONITOR);
		keyFilter = properties.getProperty(CONFIG_KEY_FILTER, DEFAULT_KEY_FILTER);
		serverUrl = properties.getProperty(CONFIG_SERVER_URL, DEFAULT_SERVER_URL);
	}

	static void validateConfiguration(Properties properties) {
		ConfigurationHelper.validateDirectoryToMonitor(
				properties.getProperty(CONFIG_DIRECTORY_TO_MONITOR, DEFAULT_DIRECTORY_TO_MONITOR));
		ConfigurationHelper.validateKeyFilter(
				properties.getProperty(CONFIG_KEY_FILTER, DEFAULT_KEY_FILTER));
		ConfigurationHelper.validateServerUrl(
				properties.getProperty(CONFIG_SERVER_URL, DEFAULT_SERVER_URL));
	}

}
