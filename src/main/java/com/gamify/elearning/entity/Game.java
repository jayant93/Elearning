package com.gamify.elearning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.ideyatech.opentides.core.entity.BaseEntity;
import com.ideyatech.opentides.core.entity.SystemCodes;
import com.ideyatech.opentides.core.util.StringUtil;
import com.ideyatech.opentides.core.web.json.Views;

@Entity
@Table(name="GAME")
public class Game extends BaseEntity{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonView(Views.SearchView.class)
	@Column(name = "TITLE")
	private String title;
	
	@JsonView(Views.SearchView.class)
	@Column(name = "DESCRIPTION")
	private String description;
	
	@JsonView(Views.SearchView.class)
	@Column(name = "URL")
	private String url;

	@JsonView(Views.SearchView.class)
	@Column(name = "ZIP_URL")
	private String zipUrl;

	@JsonView(Views.SearchView.class)
	@Column(name = "ZIP_ETAG")
	private String zipEtag;
	
	@JsonView(Views.SearchView.class)
	@Column(name = "THUMBNAIL_URL")
	private String thumbnailUrl;
	
	/**
	 * null if it is a baseGame
	 */
	@JsonView(Views.SearchView.class)
	@ManyToOne
	@JoinColumn(name = "BASE_GAME_ID")
	@JsonBackReference
	private Game baseGame;
	

	@JsonView(Views.SearchView.class)
	@ManyToOne
	@JoinColumn(name = "GENRE_ID")
	private SystemCodes genre;
	
	@JsonView(Views.SearchView.class)
	@ManyToOne
	@JoinColumn(name = "GAME_ORIENTATION")
	private SystemCodes gameOrientation;
	
	@JsonView(Views.SearchView.class)
	@Column(name = "GAME_DOCUMENT_URL")
	private String gameDocumentUrl;
	
	@JsonView(Views.SearchView.class)
	@Column(name = "GAME_MANIFEST_URL")
	private String gameManifestUrl;

	@JsonView(Views.SearchView.class)
	@Column(name = "GAME_TEMPLATE_MANIFEST_URL")
	private String gameTemplateManifestUrl;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getZipUrl() {
		return zipUrl;
	}

	public void setZipUrl(String zipUrl) {
		this.zipUrl = zipUrl;
	}

	public String getZipEtag() {
		return zipEtag;
	}

	public void setZipEtag(String zipEtag) {
		this.zipEtag = zipEtag;
	}

	public String getGameDocumentUrl() {
		return gameDocumentUrl;
	}

	public void setGameDocumentUrl(String gameDocumentUrl) {
		this.gameDocumentUrl = gameDocumentUrl;
	}

	public Game getBaseGame() {
		return baseGame;
	}

	public void setBaseGame(Game baseGame) {
		this.baseGame = baseGame;
	}

	public String getGameManifestUrl() {
		return gameManifestUrl;
	}

	public void setGameManifestUrl(String gameManifestUrl) {
		this.gameManifestUrl = gameManifestUrl;
	}

	public String getGameTemplateManifestUrl() {
		return gameTemplateManifestUrl;
	}

	public void setGameTemplateManifestUrl(String gameTemplateManifestUrl) {
		this.gameTemplateManifestUrl = gameTemplateManifestUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public SystemCodes getGameOrientation() {
		return gameOrientation;
	}

	public void setGameOrientation(SystemCodes gameOrientation) {
		this.gameOrientation = gameOrientation;
	}
	
	public SystemCodes getGenre() {
		return genre;
	}

	public void setGenre(SystemCodes genre) {
		this.genre = genre;
	}

	public String getGameBaseUrl() {
		if (!StringUtil.isEmpty(getUrl())) {
			return getUrl().replace("index.html", "");
		} else {
			if (getBaseGame() != null)
				return getBaseGame().getGameBaseUrl().replace(
						getBaseGame().getId(),
						getId());
			else
				return null;
		}
	}
	
	public String getIndexSublocation() {
		if (getGameBaseUrl().split(getId().toString() + "/").length > 1)
			return getGameBaseUrl().split(getId().toString() + "/")[1];
		else
			return "";
	}
	
}
