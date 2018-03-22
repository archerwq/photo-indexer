package io.github.archerwq.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class GpsLocation {
	private String latRef;
	private String latitude;
	private String longRef;
	private String longitude;

	@Override
	public String toString() {
		return String.format("%s %s, %s %s", latRef, latitude, longRef, longitude);
	}

	public double getLatDecimal() {
		if (Strings.isNullOrEmpty(latRef) || Strings.isNullOrEmpty(latitude)) {
			return 0;
		}
		String[] values = latitude.split("°|'|\"");
		Preconditions.checkState(values.length == 3);
		double d = Double.parseDouble(values[0]);
		double m = Double.parseDouble(values[1]);
		double s = Double.parseDouble(values[2]);
		return d > 0 ? d + m / 60 + s / 3600 : d - m / 60 - s / 3600;
	}

	public double getLongDecimal() {
		if (Strings.isNullOrEmpty(longRef) || Strings.isNullOrEmpty(longitude)) {
			return 0;
		}
		String[] values = longitude.split("°|'|\"");
		Preconditions.checkState(values.length == 3);
		double d = Double.parseDouble(values[0]);
		double m = Double.parseDouble(values[1]);
		double s = Double.parseDouble(values[2]);
		return d > 0 ? d + m / 60 + s / 3600 : d - m / 60 - s / 3600;
	}

	public String getLatRef() {
		return latRef;
	}

	public void setLatRef(String latRef) {
		this.latRef = latRef;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongRef() {
		return longRef;
	}

	public void setLongRef(String longRef) {
		this.longRef = longRef;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
