package is.merkor.statistics.hibernate;

import is.merkor.statistics.utils.MerkorLogger;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.apache.log4j.Logger;

/**
 * Creates a {@link org.hibernate.SessionFactory} from the config file {@code hibernate.cfg.xml}. 
 * 
 * @author Anna B. Nikulasdottir
 * @version 1.0
 *
 */
public class HibernateUtil {
	
	//private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class);
	private static Logger LOGGER;
	private static SessionFactory sessionFactory;

	static {
		MerkorLogger.configureLogger();
		LOGGER = Logger.getLogger(HibernateUtil.class);
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			
			sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
		} catch (Throwable e) {
			LOGGER.error("Initial SessionFactory creation failed. ", e);
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public static void setSessionFactory (SessionFactory factory) {
		sessionFactory = factory;
	}
	public static void shutdown () {
		//close caches and connection pools
		getSessionFactory().close();
	}

}
