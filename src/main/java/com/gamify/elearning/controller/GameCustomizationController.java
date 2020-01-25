package com.gamify.elearning.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gamify.elearning.entity.Game;
import com.gamify.elearning.repository.GameRepository;
import com.gamify.elearning.util.GameManifestUtil;
import com.ideyatech.opentides.core.web.controller.BaseRestController;

@BasePathAwareController
//@RestController
@RequestMapping("/api/game/customization")
public class GameCustomizationController extends BaseRestController<Game> {
	private static Logger _log = Logger.getLogger(GameCustomizationController.class);

	@Autowired
	private GameRepository gameRepository;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getBaseGameUrl(@PathVariable("id") String gameId) {
		String baseGameUrl = null;
		Game game = gameRepository.findOne(gameId);
		if(game != null){
			baseGameUrl = game.getGameBaseUrl();
		}
		return ResponseEntity.ok(baseGameUrl);
	}
	
	@RequestMapping(value = "/download-game-manifest/{id}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String loadManifest(@PathVariable("id") String id,
			@RequestParam(required=false, value="loadTemplate") boolean loadTemplate, HttpServletRequest request){
		Game game= gameRepository.findOne(id);
		String uri = loadTemplate ? game.getGameTemplateManifestUrl() : game.getGameManifestUrl();
		_log.debug("Getting manifest file from " + uri);
		StringBuffer url = new StringBuffer(uri);
		url.append("?v=").append(new Date().getTime());
		_log.info("Proxied url is " + url.toString());
		
		URLConnection conn = null;
		String result = null;
		try {
			conn = new URL(url.toString()).openConnection();
			conn.setUseCaches(false);
			result = GameManifestUtil.inputStreamToJSON(conn.getInputStream());
		} catch (MalformedURLException e) {
			_log.error("Error in downloading manifest file using default URL. Returning nothing.", e);
		} catch (IOException e) {
			_log.error("Error in downloading manifest file using default URL. Returning nothing.", e);
		}
		return result;
	}
	
	
}
