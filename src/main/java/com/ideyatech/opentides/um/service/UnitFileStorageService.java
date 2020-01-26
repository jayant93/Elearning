package com.ideyatech.opentides.um.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.codecommit.model.FileContentRequiredException;
import com.gamify.elearning.dto.FileupodeDto;
import com.gamify.elearning.entity.Unit;
import com.gamify.elearning.repository.jpa.UnitRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UnitFileStorageService {

	@Autowired
	UnitRepository unitrepo;
	
	private final Path fileStorageLocation = null;
	

	public String storeFile(MultipartFile file, String id) throws Exception {

		// op

		String address = "/images/";
		byte[] bytes = file.getBytes();
		Random t = new Random();
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		
		String currentDirectory = System.getProperty("user.dir");
	    System.out.println("user.dir: " + currentDirectory);
		String destination = System.getProperty("user.dir");
		
		
		String fullPath = destination + "/webapps" + address;
		System.out.println("path" + fullPath);
		
		 //String serverPath = "http://ec2-3-84-154-41.compute-1.amazonaws.com:8080/images/"+fileName;
		String serverPath ="http://localhost:8080/home/vijay/Desktop/elerning12-01-20/op07/archlatform/webapps/"+fileName;
	
		// File dir = new File(fullPath+ File.separator + fileName);
		//File dir = new File(fullPath + File.separator);
		//if (!dir.exists())
		//	dir.mkdirs();
		//File serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);

       // BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
		//stream.write(bytes);
		
		Unit dbFile = unitrepo.findOne(id);
		String s1 = Base64.getEncoder().encodeToString(bytes);
		dbFile.setFileBase64(s1);
		dbFile.setFileType(file.getContentType());
		dbFile.setFileName(fileName);
		//dbFile.setTextDescription(filedto.getTextDescription());
		
		dbFile.setImageLocation(serverPath);
		unitrepo.save(dbFile);
		/// op
		// return null;
		// Normalize file name

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
			}

			return fileName;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Exception("Could not store file " + fileName + ". Please try again!", e);
		}
	}

	public String getUnitFile(String id) {
		Unit dbFile = unitrepo.findOne(id);
		return dbFile.getFileBase64();
	}
	
	

	public String storedescreption(FileupodeDto filedto) {
		// TODO Auto-generated method stub
		String id = filedto.getId();
		Unit descr = unitrepo.findOne(id);
		if(descr != null) {
			descr.setTextDescription(filedto.getTextDescription());
			unitrepo.save(descr);
		}
		else {
			return "update fail";	
		}
		return "update suuccessfully";
	}
	
//	public String storeFile(MultipartFile file, String id, FileupodeDto filedto) throws Exception {
//		 // Normalize file name
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//        try {
//            // Check if the file's name contains invalid characters
//            if(fileName.contains("..")) {
//            	throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
//            }
//
//            // Copy file to the target location (Replacing existing file with the same name)
//            Path targetLocation = this.fileStorageLocation.resolve(fileName);
//            System.out.println("location  "+targetLocation);
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//            return fileName;
//        } catch (IOException ex) {
//        	throw new Exception("Could not store file " + fileName + ". Please try again!", ex);
//        }
//}

}
