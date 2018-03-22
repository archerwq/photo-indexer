package io.github.archerwq.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.archerwq.cmd.Config;
import io.github.archerwq.model.PhotoUgi;

public class PhotoDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(PhotoDao.class);

	private BasicDataSource dataSource;

	public PhotoDao() {
		dataSource = new BasicDataSource();
		dataSource.setUrl(Config.DB_URL);
		dataSource.setUsername(Config.DB_USER);
		dataSource.setPassword(Config.DB_PWD);
		dataSource.setMinIdle(Config.DB_CONN_MIN_IDLE);
		dataSource.setMaxIdle(Config.DB_CONN_MAX_IDLE);
	}

	public void cleanup() {
		try {
			dataSource.close();
		} catch (SQLException e) {
			LOGGER.warn("failed to close datasource");
		}
	}

	/**
	 * Return null if it does not exist.
	 * 
	 * @param sha1
	 * @return
	 * @throws Exception
	 */
	private PhotoUgi getUgi(String sha1) throws Exception {
		String sql = "SELECT tags, story, updated_on FROM ugi WHERE sha1=?";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, sha1);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.first()) {
					String tags = rs.getString("tags");
					String story = rs.getString("story");
					long updatedOn = rs.getLong("updated_on");

					PhotoUgi ugi = new PhotoUgi();
					ugi.setSha1(sha1);
					if (tags != null) {
						Arrays.stream(tags.split(",")).forEach(tag -> ugi.addTag(tag));
					}
					ugi.setStory(story);
					ugi.setUpdatedOn(updatedOn);
					return ugi;
				}

				return null;
			}
		}
	}

	/**
	 * Add tags for a photo.
	 * 
	 * @param sha1
	 * @param tags
	 * 
	 * @throws Exception
	 */
	public void addTags(String sha1, String[] tags) throws Exception {
		PhotoUgi ugi = getUgi(sha1);
		long time = System.currentTimeMillis();
		if (ugi == null) {
			String sql = "INSERT INTO ugi(sha1, tags, story, updated_on) VALUES(?, ?, NULL, ?)";
			String newTags = StringUtils.join(tags, ",");
			try (Connection connection = dataSource.getConnection();
					PreparedStatement ps = connection.prepareStatement(sql);) {
				ps.setString(1, sha1);
				ps.setString(2, newTags);
				ps.setLong(3, time);
				ps.executeUpdate();
			}
		} else {
			String sql = "UPDATE ugi SET tags=?, updated_on=? WHERE sha1=?";
			ugi.getTags().addAll(Arrays.asList(tags));
			String newTags = ugi.getTags().stream().collect(Collectors.joining(","));
			try (Connection connection = dataSource.getConnection();
					PreparedStatement ps = connection.prepareStatement(sql);) {
				ps.setString(1, newTags);
				ps.setLong(2, time);
				ps.setString(3, sha1);
				ps.executeUpdate();
			}
		}
	}

	/**
	 * Add story for a photo.
	 * 
	 * @param sha1
	 * @param story
	 * @throws Exception
	 */
	public void addStory(String sha1, String story) throws Exception {
		PhotoUgi ugi = getUgi(sha1);
		long time = System.currentTimeMillis();
		if (ugi == null) {
			String sql = "INSERT INTO ugi(sha1, tags, story, updated_on) VALUES(?, NULL, ?, ?)";
			try (Connection connection = dataSource.getConnection();
					PreparedStatement ps = connection.prepareStatement(sql);) {
				ps.setString(1, sha1);
				ps.setString(2, story);
				ps.setLong(3, time);
				ps.executeUpdate();
			}
		} else {
			String sql = "UPDATE ugi SET story=?, updated_on=? WHERE sha1=?";
			try (Connection connection = dataSource.getConnection();
					PreparedStatement ps = connection.prepareStatement(sql);) {
				ps.setString(1, story);
				ps.setLong(2, time);
				ps.setString(3, sha1);
				ps.executeUpdate();
			}
		}
	}

	/**
	 * Get UGI records.
	 * 
	 * @param startTime
	 * @return list of PhotoUGI ordered by updatedOn asc
	 * @throws Exception
	 */
	public List<PhotoUgi> getUgis(long startTime, long endTime, int offset, int limit) throws Exception {
		String sql = "SELECT sha1, tags, story, updated_on FROM ugi WHERE updated_on>=? AND updated_on<=? "
				+ "ORDER BY updated_on asc limit ? offset ?";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setLong(1, startTime);
			ps.setLong(2, endTime);
			ps.setInt(3, limit);
			ps.setInt(4, offset);
			try (ResultSet rs = ps.executeQuery()) {
				List<PhotoUgi> result = new ArrayList<>();
				while (rs.next()) {
					String sha1 = rs.getString("sha1");
					String tags = rs.getString("tags");
					String story = rs.getString("story");
					long updatedOn = rs.getLong("updated_on");

					PhotoUgi ugi = new PhotoUgi();
					ugi.setSha1(sha1);
					if (tags != null) {
						Arrays.stream(tags.split(",")).forEach(tag -> ugi.addTag(tag));
					}
					ugi.setStory(story);
					ugi.setUpdatedOn(updatedOn);
					result.add(ugi);
				}
				return result;
			}
		}
	}

	/**
	 * Check if the given photo meta has been indexed.
	 * 
	 * @param sha1
	 *            sha1 of the photo to be checked
	 * @return the path of the photo if it's been indexed, null or else
	 * @throws Exception
	 */
	public String metaIndexed(String sha1) throws Exception {
		String sql = "SELECT path FROM meta_indexed WHERE sha1=?";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, sha1);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.first()) {
					return rs.getString("path");
				}
				return null;
			}
		}
	}

	/**
	 * Add indexed photo meta.
	 * 
	 * @param sha1
	 * @param path
	 *            photo path
	 * @throws Exception
	 */
	public void addIndexedMeta(String sha1, String path) throws Exception {
		String sql = "INSERT into meta_indexed(sha1, path, indexed_on) VALUES(?, ?, ?)";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, sha1);
			ps.setString(2, path);
			ps.setLong(3, System.currentTimeMillis());
			ps.executeUpdate();
		}
	}

	/**
	 * Updated indexed photo meta.
	 * 
	 * @param sha1
	 * @param path
	 *            photo path
	 * @throws Exception
	 */
	public void updateIndexedMeta(String sha1, String path) throws Exception {
		String sql = "UPDATE meta_indexed set path=?, indexed_on=? where sha1=?";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, path);
			ps.setLong(2, System.currentTimeMillis());
			ps.setString(3, sha1);
			ps.executeUpdate();
		}
	}

}
