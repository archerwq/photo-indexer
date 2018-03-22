package io.github.archerwq.index;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.archerwq.dao.PhotoDao;
import io.github.archerwq.model.PhotoMeta;

public class MetaIndexTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetaIndexTask.class);

	private String[] dirs;
	private EsIndexer esIndexer;
	private PhotoDao dao;

	public MetaIndexTask(String[] dirs, EsIndexer esIndexer, PhotoDao dao) {
		this.dirs = dirs;
		this.esIndexer = esIndexer;
		this.dao = dao;
	}

	public void index() {
		for (String dir : dirs) {
			File root = new File(dir);
			if (!root.exists()) {
				LOGGER.warn("dir does not exist: {}", dir);
				return;
			}
			index(root);
		}
	}

	private void index(File dir) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				index(f);
			}
			LOGGER.info(f.getAbsolutePath());
			try {
				String path = f.getAbsolutePath();
				PhotoMeta photo = PhotoMeta.load(path);
				String indexedPath = dao.metaIndexed(photo.getSha1());
				if (indexedPath == null) {
					esIndexer.addPhotoMeta(photo);
					dao.addIndexedMeta(photo.getSha1(), path);
				} else if (!indexedPath.equals(path)) {
					esIndexer.addPhotoMeta(photo);
					dao.updateIndexedMeta(photo.getSha1(), path);
				}
			} catch (Exception e) {
				LOGGER.warn("failed to index photo: {}", f.getAbsolutePath(), e);
			}
		}
	}

}
