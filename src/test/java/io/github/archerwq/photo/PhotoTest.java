package io.github.archerwq.photo;

import org.junit.Test;

import io.github.archerwq.model.PhotoMeta;

public class PhotoTest {

	public static PhotoMeta newTestPhoto() throws Exception {
		return PhotoMeta.load("/Users/qiangwang/Pictures/huawei_phone_bkup/camera/IMG_20171217_081102.jpg");
	}

	@Test
	public void testLoad() throws Exception {
		PhotoMeta photo = PhotoTest.newTestPhoto();
		System.out.println(photo);
	}

	@Test
	public void testLoad1() throws Exception {
		PhotoMeta photo = PhotoMeta.load("/Users/qiangwang/Desktop/Screen Shot 2018-02-11 at 11.04.48 AM.png");
		System.out.println(photo);
	}

}
