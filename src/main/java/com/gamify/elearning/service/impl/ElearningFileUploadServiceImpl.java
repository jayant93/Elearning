package com.gamify.elearning.service.impl;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.gamify.elearning.entity.GameFile;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.gamify.elearning.service.ElearningFileUploadService;

@Service
public class ElearningFileUploadServiceImpl implements ElearningFileUploadService{


	private static Logger _log = Logger.getLogger(ElearningFileUploadServiceImpl.class);
    
    @Value("${s3.accessKey}")
    private String accessKey;
    
    @Value("${s3.secretKey}")
    private String secretKey;

	@Value("${s3.bucketName}")
	private String bucketName;

	@Value("${s3.endpoint}")
	private String amazonURL;

	@Value("${s3.domain}")
	private String s3Domain;

	@Value("${cloudfront.distributionId}")
	private String cloudFrontDistributionId;
	
	private AmazonS3 s3Client;

	private AmazonCloudFront cloudFront;

	private TransferManager transferManager;
	
	@PostConstruct
    private void initializeAmazon() {
       AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
       s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_1).build();
       cloudFront = AmazonCloudFrontClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_1).build();
       transferManager = TransferManagerBuilder.standard().withS3Client(s3Client).build();
	}

	/**
	 * 
	 */
	@Override
	public String upload(String filePath, File file) {
		_log.info("Uploading [" + filePath + "] to Amazon S3...");
		PutObjectResult uploadResult =s3Client.putObject(bucketName, filePath, file);

		try {
			_log.info("uploaded "+ file.getName() + " with S3 E-tag value of" + uploadResult.getETag());
		} catch (AmazonServiceException e) {
			_log.error("Error in accessing Amazon service.", e);
		} catch (AmazonClientException e) {
			_log.error("Error in accessing Amazon Client service.", e);
		}

		// set the newly uploaded file to public
		s3Client.setObjectAcl(bucketName, filePath, CannedAccessControlList.PublicRead);

		try {
			CreateInvalidationResult invalidation = invalidate("/" + filePath);
			_log.info("Invalidating file [" + file + "] in Cloudfront: " + invalidation.toString());
		} catch (URISyntaxException e) {
			_log.error("Cannot encode the file path for invalidation. Invalidation request failed.", e);
		}


		return s3Domain + "/" +filePath;
	}

	@Override
	public void upload(List<GameFile> gameFiles, String outputFolder, String parentFolder) {
		int i = 1, max = gameFiles.size();


		_log.info("Uploading " + max + " files to folder " + parentFolder);

		for (GameFile gameFile : gameFiles) {

			_log.info("Uploading [" + gameFile.getPath().replace(outputFolder, "") + "] to Amazon S3...");

			Upload upload = transferManager.upload(bucketName, gameFile.getPath().replace(outputFolder, ""),
					gameFile.getFile());

			try {
				upload.waitForCompletion();

			} catch (AmazonServiceException e) {
				_log.error("Error in accessing Amazon service.", e);
			} catch (AmazonClientException e) {
				_log.error("Error in accessing Amazon Client service.", e);
			} catch (InterruptedException e) {
				_log.error("Amazon connection interrupted.", e);
			}

			// set the newly uploaded file to public
			transferManager.getAmazonS3Client().setObjectAcl(bucketName, gameFile.getPath().replace(outputFolder, ""),
					CannedAccessControlList.PublicRead);

			++i;
		}

		try {
			// invalidate all the contents of the parent folder
			CreateInvalidationResult invalidation = invalidate("/" + parentFolder + "*");
			_log.info("Invalidating folder [" + parentFolder + "] in Cloudfront: " + invalidation.toString());
		} catch (URISyntaxException e) {
			_log.error("Cannot encode the file path for invalidation. Invalidation request failed.", e);
		}

		_log.info("Finished uploading game file to Amazon S3.");

	}

	@Override
	public void unZipFile(String zipFileLocation, String outputFolder) {
		try (ZipFile zipFile = new ZipFile(zipFileLocation)){
			Enumeration<? extends ZipEntry> enu = zipFile.entries();

			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = enu.nextElement();

				String name = zipEntry.getName();
				File file = new File(outputFolder, name);
				if (name.endsWith("/")) {
					file.mkdirs();
					continue;
				}

				File parent = file.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}

				// enclose in try-resource clause so that no stream will be left opened
				try (InputStream is = zipFile.getInputStream(zipEntry);
					 OutputStream fos = new FileOutputStream(file)) {
					IOUtils.copy(is, fos);
				} catch (IOException e) {
					_log.error("IOException. Error while writing file content " + zipEntry.getName(), e);
				}
			}
		} catch (IOException e) {
			_log.error("IOException. Error while unzipping file.", e);
		}
	}


	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws URISyntaxException 
	 */
	private CreateInvalidationResult invalidate(String filePath) throws URISyntaxException {
		List<String> files = new ArrayList<>();

		// we need to encode the non-ASCII or unsafe characters in the path as defined in RFC 1783
		final String asciiString = new URI(filePath).toASCIIString();
		files.add(asciiString);

		_log.info("Creating invalidation request for file " + asciiString);

		return invalidate(files);
	}

	/**
	 * 
	 * @param filePaths
	 * @return
	 */
	private CreateInvalidationResult invalidate(List<String> filePaths) {
		_log.debug("Creating invalidation requests for " + filePaths.size() + " files.");

		Paths files = new Paths().withItems(filePaths).withQuantity(filePaths.size());

		CreateInvalidationRequest invalidation = new CreateInvalidationRequest(cloudFrontDistributionId,
				new InvalidationBatch(files, cloudFrontDistributionId + System.currentTimeMillis()));

		_log.debug("Invalidation requested: " + invalidation.toString());
		return cloudFront.createInvalidation(invalidation);
	}
}
