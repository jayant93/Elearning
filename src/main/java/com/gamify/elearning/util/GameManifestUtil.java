package com.gamify.elearning.util;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gamify.elearning.entity.Game;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class for game manifest files
 * 
 * @author AJ
 *
 */
public class GameManifestUtil {
	
    private static Logger _log = Logger.getLogger(GameManifestUtil.class);
    
	/**
	 * Generates a File object of the manifest file using the zip file
	 * 
	 */
	public static File generateManifestFile(Game game, List<File> files, String tempDir, String outputFolder) {
			
			String jsonManifest = generateManifestJSON(game, files, outputFolder);
			
			try {
				File file = new File(tempDir, "manifest.json");
				_log.info("Creating temporary file: " + file.getName());
				_log.info("Path: " + file.getPath());
				_log.info("Absolute Path: " + file.getAbsolutePath());
				_log.info("Canonical Path: " + file.getCanonicalPath());
				
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(jsonManifest);
				output.close();
				
				return file;
				
			} catch (IOException e) {
                _log.error("Error in accessing template.json file from tmp folder.", e);
			}
		
		return null;
	}
	
	/**
	 * Generates a JSON object of the manifest file using the zip file
	 * @throws JSONException 
	 * 
	 */
	public static String generateManifestJSON(Game game, List<File> files, String outputFolder){
		ObjectMapper gameJson = Jackson.getObjectMapper();
		JsonNode rootNode = gameJson.createObjectNode();
		((ObjectNode) rootNode).put("name", game.getTitle());
		((ObjectNode) rootNode).put("description", game.getDescription());
		try {
			if("GAME_ORIENTATION_PORTRAIT".equals(game.getGameOrientation().getKey())) {
				((ObjectNode) rootNode).put("width", 640);
				((ObjectNode) rootNode).put("height", 1136);
			} else {
				((ObjectNode) rootNode).put("width", 1136);
				((ObjectNode) rootNode).put("height", 640);
			}
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			((ObjectNode) rootNode).put("width", 1136);
			((ObjectNode) rootNode).put("height", 640);
		}
		ArrayNode screens = gameJson.createArrayNode();
		JsonNode screen1 = gameJson.createObjectNode();
		((ObjectNode) screen1).put("name", "Default");
		
		ArrayNode images = gameJson.createArrayNode();
		String sublocation = game.getIndexSublocation();
		
		
		
		if(files != null)
		for (File file : files) {
		
			String fullPath = file.getPath().replace("\\", "/").replace(outputFolder, "");
			String name = fullPath.replace(sublocation, "");
			_log.trace("Processing file " + name);
			
			if(name.endsWith(".jpeg") ||
					name.endsWith(".jpg") || 
					name.endsWith(".png") || 
					name.endsWith(".gif")) {
				
				int width = 0, height = 0;
				
				try {
					BufferedImage bi = ImageIO.read(file);
					width = bi.getWidth();
					height = bi.getHeight();
				} catch (IOException e) {
                    _log.error("Error in accessing manifest file: " + name, e);
				}
				
				JsonNode image = gameJson.createObjectNode();
				((ObjectNode)image).put("image", name);
				((ObjectNode)image).put("notes", "");
				((ObjectNode)image).put("width", width);
				((ObjectNode)image).put("height", height);
				
				ArrayNode locations = gameJson.createArrayNode();
				JsonNode location1 = gameJson.createObjectNode();
				((ObjectNode) location1).put("x", 0);
				((ObjectNode) location1).put("y", 0);
				((ObjectNode) location1).put("cropCoordX", 0);
				((ObjectNode) location1).put("cropCoordY", 0);
				((ObjectNode) location1).put("cropCoordX2", width);
				((ObjectNode) location1).put("cropCoordY2", height);
				locations.add(location1);
				
				((ObjectNode)image).putArray("locations").addAll(locations);
				
				images.add(image);
			}
		}
		
		((ObjectNode) screen1).putArray("images").addAll(images);
		screens.add(screen1);
		((ObjectNode) rootNode).putArray("screens").addAll(screens);
		
		return rootNode.toString();
		
	}

	/**
	 * Generates a JSON object of the manifest file using the zip file
	 * @throws JSONException 
	 * 
	 */
	public static String validateManifestJSON(InputStream is) {
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] bytes = new byte[4096];
		    for(int len;(len = is.read(bytes))>0;)
		        baos.write(bytes, 0, len);
		    String json = new String(baos.toByteArray(), "UTF-8");

		    
			try {
				
				ObjectMapper mapper = new ObjectMapper();
				 mapper.readTree(json);
			} catch (Exception e) {
                _log.error("Error in retrieving JSON objects from manifest file.", e);
				return e.getMessage();
			}
	
		} catch (Exception e){
            _log.error("Error in accessing manifest file.", e);
			return "invalid-file";
		}
	    
		
		return "valid";
	}
	
/*	*//**
	 * Generates a JSON object of the manifest file using the zip file
	 * @throws JSONException 
	 * 
	 */
	public static String inputStreamToJSON(InputStream is) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			byte[] bytes = new byte[4096];
				for (int len; (len = is.read(bytes)) > 0;)
					baos.write(bytes, 0, len);
			
			String json = new String(baos.toByteArray(), "UTF-8");
			//System.out.println(json);
	
			return json;
			
		} catch (IOException e) {
            _log.error("Error in accessing manifest file.", e);
			return null;
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
			}
		}
		
	}
	
}
