package io.github.archerwq.photo;

import org.junit.Assert;
import org.junit.Test;

import io.github.archerwq.model.GpsLocation;

public class GpsLocationTest {

	@Test
	public void testGetLatLongDecimal() {
		GpsLocation loc1 = new GpsLocation();
		loc1.setLatRef("N");
		loc1.setLatitude("22° 21' 25.08\"");
		loc1.setLongRef("E");
		loc1.setLongitude("113° 30' 38.56\"");
		Assert.assertEquals(22.35696666666667d, loc1.getLatDecimal(), 0.0001);
		Assert.assertEquals(113.5107111111111d, loc1.getLongDecimal(), 0.0001);

		GpsLocation loc2 = new GpsLocation();
		loc2.setLatRef("S");
		loc2.setLatitude("22° 21' 25.08\"");
		loc2.setLongRef("W");
		loc2.setLongitude("113° 30' 38.56\"");
		Assert.assertEquals(-22.35696666666667d, loc2.getLatDecimal(), 0.0001);
		Assert.assertEquals(-113.5107111111111d, loc2.getLongDecimal(), 0.0001);
	}

}
