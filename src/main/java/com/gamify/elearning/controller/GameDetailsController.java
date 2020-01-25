package com.gamify.elearning.controller;

import java.util.List;

import javax.transaction.Transactional;

import com.gamify.elearning.service.GameService;
import com.ideyatech.opentides.core.repository.SystemCodesRepository;
import com.ideyatech.opentides.um.util.BeanUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.gamify.elearning.dto.GameDetailsDTO;
import com.gamify.elearning.entity.Game;
import com.gamify.elearning.repository.GameRepository;
import com.ideyatech.opentides.core.entity.SystemCodes;
import com.ideyatech.opentides.core.web.controller.BaseRestController;

@BasePathAwareController
//@RestController
@RequestMapping("/api/game/details")
public class GameDetailsController extends BaseRestController<Game>{
	private static Logger _log = Logger.getLogger(GameDetailsController.class);

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GameService gameService;

	@Autowired
	private SystemCodesRepository systemCodesRepository;

	
	@Value("${temp.dir}")
	private String tempDir;

	@GetMapping(value = "")
	public ResponseEntity<?> getGameDetails(@RequestParam("gameId") String id){
		Game game = gameRepository.findOne(id);

		GameDetailsDTO gameDetails = new GameDetailsDTO();
		BeanUtil.copyProperties(game, gameDetails);
		gameDetails.setGenreKey(game.getGenre().getKey());

		return ResponseEntity.ok(gameDetails);
	}

	@GetMapping(value = "/genres")
	public @ResponseBody ResponseEntity<?> getGenres(){
		List<SystemCodes> genres = systemCodesRepository.findByCategory("GAME_GENRE");
		return ResponseEntity.ok(genres);
	} 
	
	@Transactional
	@RequestMapping(value = "/save", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody ResponseEntity<?> saveGameDetails(@RequestBody GameDetailsDTO gameDetailsDTO){
		Game game = new Game();
		BeanUtil.copyProperties(gameDetailsDTO, game);
		game.setGenre(systemCodesRepository.findByKey(gameDetailsDTO.getGenreKey()));
		gameRepository.save(game);
		return ResponseEntity.ok(game.getId());
		
	}
	
	@Transactional
	@RequestMapping(value = "/{id}/upload-thumbnail", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> uploadThumbnail(@PathVariable String id, @RequestParam("file") MultipartFile thumbnail){
		String response = this.gameService.uploadThumbnail(id, thumbnail);
		return ResponseEntity.ok(response);
		
	}
	
}
