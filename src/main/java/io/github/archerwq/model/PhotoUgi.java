package io.github.archerwq.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Strings;

public class PhotoUgi {
	private String sha1;
	private Set<String> tags = Collections.synchronizedSet(new HashSet<>());
	private String story;
	private long updatedOn;

	@Override
	public String toString() {
		return String.format("sha1=[%s], tags=[%s], story=[%s], updatedOn=[%d]", sha1, tags, story, updatedOn);
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public void addTag(String tag) {
		if (!Strings.isNullOrEmpty(tag)) {
			tags.add(tag);
		}
	}

	public void setStory(String story) {
		this.story = story;
	}

	public String getSha1() {
		return sha1;
	}

	public Set<String> getTags() {
		return tags;
	}

	public String getStory() {
		return story;
	}

	public long getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(long updatedOn) {
		this.updatedOn = updatedOn;
	}
}
