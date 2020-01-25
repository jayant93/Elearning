package com.gamify.elearning.dto;

public class GameDetailsDTO {
	private String id;
	private String title;
	private String description;
	private String genreKey;
	private String thumbnailUrl;
	private String zipUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGenreKey() {
		return genreKey;
	}

	public void setGenreKey(String genreKey) {
		this.genreKey = genreKey;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getZipUrl() {
		return zipUrl;
	}

	public void setZipUrl(String zipUrl) {
		this.zipUrl = zipUrl;
	}
}
