package io.github.archerwq.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import io.github.archerwq.model.GpsLocation;
import io.github.archerwq.model.PhotoMeta;

public class EsIndexer {
	private static final Logger LOGGER = LoggerFactory.getLogger(EsIndexer.class);

	private RestHighLevelClient client;

	/**
	 * Initialize the indexer.
	 * 
	 * @param esUrl
	 *            ES connection URL
	 */
	public void init(String esUrl) {
		client = new RestHighLevelClient(RestClient.builder(HttpHost.create(esUrl)));
	}

	public void cleanup() {
		try {
			client.close();
		} catch (IOException e) {
			LOGGER.warn("failed to close RestClient", e);
		}
	}

	/**
	 * Create photo index with the mapping specifed by photo.json under classpath.
	 * 
	 * @throws IOException
	 */
	public void createIndex() throws IOException {
		LOGGER.info("creating photo index...");
		CreateIndexRequest request = new CreateIndexRequest("files");
		InputStream in = getClass().getClassLoader().getResourceAsStream("photo.json");
		String mapping = new String(in.readAllBytes());
		in.close();
		request.mapping("photo", mapping, XContentType.JSON);
		CreateIndexResponse response = client.indices().create(request);
		LOGGER.info("photo index created: " + response.toString());
	}

	/**
	 * Delete photo index.
	 * 
	 * @throws IOException
	 */
	public void deleteIndex() throws IOException {
		LOGGER.info("deleting photo index...");
		DeleteIndexRequest request = new DeleteIndexRequest("files");
		DeleteIndexResponse deleteIndexResponse = client.indices().delete(request);
		LOGGER.info("photo index deleted: " + deleteIndexResponse.toString());
	}

	/**
	 * Index a photo.
	 * 
	 * @param mata
	 *            photo to be indexed
	 * @throws IOException
	 */
	public void addPhotoMeta(PhotoMeta mata) throws IOException {
		Preconditions.checkNotNull(mata, "photo should not be null");
		LOGGER.info("indexing a photo: {}, {}", mata.getSha1(), mata.getPath());

		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("sha1", mata.getSha1());
		jsonMap.put("path", mata.getPath());
		jsonMap.put("time", mata.getTime());
		jsonMap.put("width", mata.getWidth());
		jsonMap.put("length", mata.getLength());
		jsonMap.put("location", toEsGeo(mata.getLocation()));
		jsonMap.put("type", mata.getType());

		IndexRequest request = new IndexRequest("files", "photo", mata.getSha1()).source(jsonMap);
		IndexResponse indexResponse = client.index(request);
		LOGGER.info("photo indexed: " + indexResponse.toString());
	}

	/**
	 * Remove a phote.
	 * 
	 * @param sha1
	 * @throws IOException
	 */
	public void deletePhoto(String sha1) throws IOException {
		Preconditions.checkArgument(StringUtils.isBlank(sha1));
		LOGGER.info("deleting a photo: {}", sha1);

		DeleteRequest request = new DeleteRequest("files", "photo", sha1);
		DeleteResponse deleteResponse = client.delete(request);
		LOGGER.info("photo deleted: " + deleteResponse.toString());

	}

	/**
	 * Add tags for a photo.
	 * 
	 * @param sha1
	 *            sha1 of the photo
	 * @param tags
	 * @param story
	 * @throws IOException
	 */
	public void addPhotoUgi(String sha1, String[] tags, String story) throws IOException {
		Preconditions.checkArgument(!StringUtils.isBlank(sha1));
		if (tags == null) {
			tags = new String[] {};
		}
		if (story == null) {
			story = "";
		}
		LOGGER.info("add ugi for a photo: {}, {}, {}", sha1, tags, story);

		UpdateRequest request = new UpdateRequest("files", "photo", sha1);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("tags", tags);
		parameters.put("story", story);
		Script inline = new Script(ScriptType.INLINE, "painless",
				"ctx._source.tags=params.tags; ctx._source.story=params.story;", parameters);
		request.script(inline);
		UpdateResponse updateResponse = client.update(request);
		LOGGER.info("photo updated: " + updateResponse.toString());
	}

	private String toEsGeo(GpsLocation location) {
		Preconditions.checkNotNull(location);
		return String.valueOf(location.getLatDecimal()) + "," + String.valueOf(location.getLongDecimal());
	}

}
