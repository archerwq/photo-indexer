package io.github.archerwq.index;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.github.archerwq.model.PhotoMeta;
import io.github.archerwq.photo.PhotoTest;

public class PhotoIndexerTest {
	private EsIndexer indexer;

	@Before
	public void setup() {
		indexer = new EsIndexer();
		indexer.init("http://localhost:9200");
	}

	@After
	public void teardown() {
		indexer.cleanup();
	}

	@Test
	@Ignore
	public void testCreateIndex() throws IOException {
		indexer.createIndex();
	}

	@Test
	@Ignore
	public void testDeleteIndex() throws IOException {
		indexer.deleteIndex();
	}

	@Test
	public void testAddPhoto() throws Exception {
		PhotoMeta photo = PhotoTest.newTestPhoto();
		indexer.addPhotoMeta(photo);
	}

	@Test
	public void testAddPhotoUgi() throws Exception {
		indexer.addPhotoUgi("4e615f805ef3dd758d2e08fb902bb1cc55688774", new String[] { "糖糖", "格力海岸", "烟花" },
				"第二次去美国出差，在公司办公室里查问题，貌似是ActiveMQ的问题");
	}

	@Test
	public void testDeletePhoto() throws IOException {
		indexer.deletePhoto("4e615f805ef3dd758d2e08fb902bb1cc55688774");
	}

}
