package com.ideyatech.opentides.um.dto;

import java.util.List;

public class Claim {

	private String key;
	private String level;
	private String title;
	private String parent;
	private List<Claim> children;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<Claim> getChildren() {
		return children;
	}

	public void setChildren(List<Claim> children) {
		this.children = children;
	}

}
