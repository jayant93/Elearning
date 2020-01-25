package com.gamify.elearning.controller;

import com.gamify.elearning.service.GameService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.gamify.elearning.entity.Game;
import com.ideyatech.opentides.core.web.controller.BaseRestController;

@BasePathAwareController
//@RestController
@RequestMapping("/api/game/upload")
public class GameUploadController extends BaseRestController<Game>{
	
	private static Logger _log = Logger.getLogger(GameUploadController.class);

	@Autowired
	private GameService gameService;

	@RequestMapping(value = "/{id}/upload-game", method = RequestMethod.POST)
	public ResponseEntity<?> uploadGame(@PathVariable("id") String id, @RequestParam("file") MultipartFile gameZip){
		String response = gameService.uploadGame(id, gameZip);
		return ResponseEntity.ok(response);
	}

}
