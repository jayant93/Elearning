package com.gamify.elearning.entity;

import java.io.File;

/**
 * Simpler wrapper class for Java File
 * 
 * @author AJ
 *
 */
public class GameFile {

	private String path;

	private File file;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public GameFile(File file, String path) {
		this.file = file;
		this.path = path;
	}
	
}