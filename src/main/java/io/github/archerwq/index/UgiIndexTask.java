package io.github.archerwq.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.archerwq.cmd.Config;
import io.github.archerwq.dao.PhotoDao;
import io.github.archerwq.model.PhotoUgi;

public class UgiIndexTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(UgiIndexTask.class);
	private static final int BATCH_SIZE = 100;

	private EsIndexer esIndexer;
	private PhotoDao dao;

	public UgiIndexTask(EsIndexer esIndexer, PhotoDao dao) {
		this.esIndexer = esIndexer;
		this.dao = dao;
	}

	public void index() {
		int offset = 0;
		int count = Integer.MAX_VALUE;
		List<PhotoUgi> ugis = null;
		while (count >= BATCH_SIZE) {
			try {
				ugis = dao.getUgis(loadUgiTimestamp(), System.currentTimeMillis(), offset, BATCH_SIZE);
			} catch (Exception e) {
				LOGGER.error("failed to get ugis", e);
				return;
			}
			ugis.forEach(ugi -> {
				try {
					esIndexer.addPhotoUgi(ugi.getSha1(), ugi.getTags().toArray(new String[ugi.getTags().size()]),
							ugi.getStory());
				} catch (IOException e) {
					LOGGER.warn("failed to add ugi: {}", ugi.toString());
				}
			});
			count = ugis.size();
			offset += count;
		}

		if (ugis.size() > 0) {
			writeUgiTimestamp(ugis.get(ugis.size() - 1).getUpdatedOn());
		}
	}

	private void writeUgiTimestamp(long timestamp) {
		try (PrintWriter pw = new PrintWriter(new FileWriter(Config.UGI_TS_PATH))) {
			pw.println(String.valueOf(timestamp));
		} catch (Exception e) {
			LOGGER.warn("failed to write ugi timestamp", e);
		}
	}

	private long loadUgiTimestamp() {
		try (BufferedReader br = new BufferedReader(new FileReader(Config.UGI_TS_PATH))) {
			String line = br.readLine();
			return Long.parseLong(line);
		} catch (Exception e) {
			LOGGER.warn("failed to load ugi timestamp", e);
			return 0;
		}
	}

}
