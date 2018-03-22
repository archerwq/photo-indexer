package io.github.archerwq.cmd;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.archerwq.dao.PhotoDao;
import io.github.archerwq.index.EsIndexer;
import io.github.archerwq.index.MetaIndexTask;
import io.github.archerwq.index.UgiIndexTask;

public class Startup {
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

	/**
	 * Start up the indexer.
	 * 
	 * @param args
	 *            <li>config file path, required</li>
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			LOGGER.error("invalid arguments");
			return;
		}

		// load config file
		try {
			Config.load(args[0]);
		} catch (ConfigurationException e) {
			LOGGER.error("failed to load: {}", args[0], e);
			return;
		}

		PhotoDao photoDao = new PhotoDao();
		EsIndexer esIndexer = new EsIndexer();
		esIndexer.init(Config.ES_URL);

		new MetaIndexTask(Config.PHOTO_DIRS, esIndexer, photoDao).index();
		new UgiIndexTask(esIndexer, photoDao).index();

		esIndexer.cleanup();
		photoDao.cleanup();

		LOGGER.info("indexer done");
	}

}
