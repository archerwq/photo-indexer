package io.github.archerwq.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class PhotoMeta {
	private String sha1;
	private String path;
	private LocalDateTime time;
	private int width;
	private int length;
	private GpsLocation location;
	private String type;

	@Override
	public String toString() {
		return String.format("sha1=[%s], path=[%s], time=[%s], width=[%d], length=[%d], location=[%s], type=[%s]", sha1,
				path, time, width, length, location, type);
	}

	public static PhotoMeta load(String path) throws IOException, NoSuchAlgorithmException, ImageProcessingException {
		PhotoMeta photo = new PhotoMeta();
		InputStream is = new FileInputStream(path);
		Metadata metadata = ImageMetadataReader.readMetadata(is);
		photo.sha1 = photo.sha1(is);
		is.close();

		photo.path = path;
		GpsLocation location = new GpsLocation();
		photo.location = location;

		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				String value = tag.getDescription();
				switch (directory.getName() + " - " + tag.getTagName()) {
				case "Exif IFD0 - Date/Time":
					photo.time = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
					break;
				case "Exif SubIFD - Date/Time Original":
					photo.time = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
					break;
				case "Exif IFD0 - Image Width":
					photo.width = Integer.parseInt(value.replace(" pixels", ""));
					break;
				case "Exif SubIFD - Exif Image Width":
					photo.width = Integer.parseInt(value.replace(" pixels", ""));
					break;
				case "Exif IFD0 - Image Height":
					photo.length = Integer.parseInt(value.replace(" pixels", ""));
					break;
				case "Exif SubIFD - Exif Image Height":
					photo.length = Integer.parseInt(value.replace(" pixels", ""));
					break;
				case "GPS - GPS Latitude Ref":
					location.setLatRef(value);
					break;
				case "GPS - GPS Latitude":
					location.setLatitude(value);
					break;
				case "GPS - GPS Longitude Ref":
					location.setLongRef(value);
					break;
				case "GPS - GPS Longitude":
					location.setLongitude(value);
					break;
				case "File Type - Detected File Type Name":
					photo.type = value;
					break;
				}
			}
		}
		return photo;
	}

	/**
	 * Return 40 chars sha1 value of the given InputStream.
	 */
	private String sha1(InputStream is) throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		int n = 0;
		byte[] buffer = new byte[8192];
		while (n != -1) {
			n = is.read(buffer);
			if (n > 0) {
				digest.update(buffer, 0, n);
			}
		}
		byte[] result = digest.digest();

		// to hex
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			String hex = Integer.toHexString(0xff & result[i]);
			if (hex.length() == 1) {
				hexString.append("0");
			}
			hexString.append(hex);
		}

		return hexString.toString();
	}

	public String getSha1() {
		return sha1;
	}

	public String getPath() {
		return path;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public int getWidth() {
		return width;
	}

	public int getLength() {
		return length;
	}

	public GpsLocation getLocation() {
		return location;
	}

	public String getType() {
		return type;
	}

}
