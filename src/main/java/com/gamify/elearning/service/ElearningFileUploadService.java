package com.gamify.elearning.service;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.gamify.elearning.entity.GameFile;


public interface ElearningFileUploadService {

		/**
		 * 
		 * @param filePath
		 * @param file
		 * @return
		 */
		public String upload(String filePath, File file);

		/**
		 * 
		 * @param gameFiles
		 *            list of {@link GameFile} that contains the assets of the game
		 * @param outputFolder
		 *            the path of temporary folder that contains the unzipped assets
		 * @param parentFolder
		 *            the parent folder of the {@literal gameFiles } in the file
		 *            server
		 */
		void upload(List<GameFile> gameFiles, String outputFolder, String parentFolder);

		// public String upload(String filePath, MultipartFile multipartFile);

		/**
		 *
		 * @param zipFileLocation
		 * @param outputFolder
		 */
		void unZipFile(String zipFileLocation, String outputFolder);
		
}