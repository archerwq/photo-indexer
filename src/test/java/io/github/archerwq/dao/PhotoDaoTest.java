package io.github.archerwq.dao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.archerwq.cmd.Config;

public class PhotoDaoTest {
	private PhotoDao dao;

	@Before
	public void setup() {
		Config.DB_URL = "jdbc:mysql://127.0.0.1:3306/photo?useUnicode=true&characterEncoding=UTF-8";
		Config.DB_USER = "qwang";
		Config.DB_PWD = "qwer1234";
		Config.DB_CONN_MIN_IDLE = 5;
		Config.DB_CONN_MAX_IDLE = 10;
		dao = new PhotoDao();
	}

	@After
	public void clean() {
		dao.cleanup();
	}

	@Test
	public void testUgi() throws Exception {
		dao.addTags("92429d82a41e930486c6de5ebda9602d55c39986", new String[] { "春节", "烟花", "阳台" });
		System.out.println(dao.getUgis(0, System.currentTimeMillis() + 1000, 0, 10));
		dao.addStory("92429d82a41e930486c6de5ebda9602d55c39986", "过年的一天晚上糖糖在阳台看海边放烟花，开始很害怕，后来就很期待了");
		System.out.println(dao.getUgis(0, System.currentTimeMillis() + 1000, 0, 10));

		dao.addStory("12429d82a41e930486c6de5ebda9602d55c39983", "过年的一天晚上糖糖在阳台看海边放烟花，开始很害怕，后来就很期待了");
		System.out.println(dao.getUgis(0, System.currentTimeMillis() + 1000, 0, 10));
		dao.addTags("12429d82a41e930486c6de5ebda9602d55c39983", new String[] { "春节", "烟花", "阳台" });
		System.out.println(dao.getUgis(0, System.currentTimeMillis() + 1000, 0, 10));
	}

	@Test
	public void testMetaInexed() throws Exception {
		dao.addIndexedMeta("92429d82a41e930486c6de5ebda9602d55c39986", "/tmp/a.jpg");
		Assert.assertEquals("/tmp/a.jpg", dao.metaIndexed("92429d82a41e930486c6de5ebda9602d55c39986"));
	}

}
