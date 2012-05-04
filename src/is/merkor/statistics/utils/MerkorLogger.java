package is.merkor.statistics.utils;

import org.apache.log4j.PropertyConfigurator;

public class MerkorLogger {
	
	private static final String logProperties = "log4j.properties";
	
	public static void configureLogger () {
		PropertyConfigurator.configure(logProperties);
		//Logger.getRootLogger().info("LogReadConfig started");
	}
}
