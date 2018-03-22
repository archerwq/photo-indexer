package io.github.archerwq.job;

import org.junit.Test;

import io.github.archerwq.cmd.Config;

public class TestConfig {
	@Test
	public void testLoad() throws Exception {
		Config.load("/Users/qiangwang/dev/java_workspace/photo-indexer/src/main/resources/indexer.conf.template");
		for (String dir : Config.PHOTO_DIRS) {
			System.out.println(dir);
		}
		System.out.println(Config.ES_URL);
	}
}
