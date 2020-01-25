package com.gamify.elearning.service.impl;
import com.gamify.elearning.entity.Game;
import com.gamify.elearning.entity.GameFile;
import com.gamify.elearning.repository.GameRepository;
import com.gamify.elearning.service.ElearningFileUploadService;
import com.gamify.elearning.service.GameService;
import com.gamify.elearning.util.GameManifestUtil;
import com.ideyatech.opentides.core.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class GameServiceImpl implements GameService {

    private static Logger _log = Logger.getLogger(GameServiceImpl.class);

    @Value("${s3.domain}")
    private String s3Domain;

    @Value("${gameAPI.url.relativePath}")
    private String apiUrl;

    @Value("${temp.dir}")
    private String tempDir;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ElearningFileUploadService elearningFileUploadService;


    @Override
    public String uploadGame(String id, MultipartFile gameZip) {
        Game game = gameRepository.findOne(id); // load the game
        if (game == null) {
            return "game doesn't exist";// in case the game is void, do nothing
        }

        File tempOutputFolder = this.createTempOutputFolder(game.getId());

        File zipFile = new File(tempDir, game.getId() + ".zip"); // create a temp file
        this.logTempFileLocation(zipFile);

        _log.debug("Game zip file received.");


        try{
            gameZip.transferTo(zipFile);
            game.setGameManifestUrl(null);
            List<GameFile> gameFiles = new ArrayList<GameFile>(); // prepare a list of game files to process
            _log.trace("Unzipping files...");
            this.elearningFileUploadService.unZipFile(zipFile.getPath(), tempOutputFolder.getPath());
            // end of iterating files on zip
            _log.trace("End of unzip.");

            _log.trace("Iterating the files in the system...");
            List<File> files = processFiles(tempOutputFolder.listFiles());

            int numberOfAssets = 0;
            String s3ParentFolder = new StringBuffer("games/").append(game.getId()).append("/").toString();

            // check if game has correct folder name for images
            boolean hasCorrectName = checkFolderName(tempOutputFolder.listFiles());

            for (File file : files) {
                // get the game file's name and sub location
                String fullPath = file.getPath().replace("\\", "/")
                        .replace(tempOutputFolder.getPath().replace("\\", "/") + "/", "");

                if(!isValid(file)) {
                    // validation has failed, skip the current file
                    _log.debug("Skipping file " + file);
                    continue;
                }

                String name = file.getName();
                String subLocation = fullPath.replace(name, "");

                if (name.endsWith(".jpeg") || name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif"))
                    numberOfAssets++; // count assets

                _log.debug("Item found: " + name + " [" + fullPath + "]");


                StringBuffer s3Path = new StringBuffer(s3ParentFolder);
                if (!StringUtil.isEmpty(subLocation))
                    s3Path.append(subLocation.substring(0, subLocation.length() - 1)).append("/");

                s3Path.append(name);

                // Additional process for index.html
                if ("index.html".equals(fullPath)) {
                    String indexUrl = this.processIndexFile(game, file, fullPath);
                    game.setUrl(indexUrl); // update the game's url (url to play the game)

                }


                // add this to the list of game files to process (declared earlier)
                gameFiles.add(new GameFile(file, s3Path.toString()));
            }


            if (StringUtil.isEmpty(game.getUrl())) {
                // index.html was not found, therefore the game is invalid. do
                // not proceed
                _log.warn("There is no index.html included in the zip.");
                return "index-not-found";
            }

            if(!hasCorrectName){
                return "wrong-img-folder-name";
            }

            _log.info("Uploading zip file to Amazon S3.");

            // upload zip file to s3
            final String filePath = "games/" + game.getId() + "/game.zip";

            String gameZipUrl = this.elearningFileUploadService.upload(filePath, zipFile);

            game.setZipUrl(gameZipUrl); // update the game's zip url (needed when downloading the game)
            game.setZipEtag(null); // remove the etag because we are sure that this has changed due to the just finished download, then force the rezip of the files on download

            _log.info("Uploading game files to S3. Number of items to upload is [" + gameFiles.size() + "]");

            this.elearningFileUploadService.upload(gameFiles,
                    "/" + tempOutputFolder.getPath().replace("\\", "/"), s3ParentFolder);


            /**
             * TEMPLATE MANIFEST
             */
            _log.debug("Generating a template manifest file...");
            // auto generate a manifest file
            File generatedTemplateManifestFile = GameManifestUtil.generateManifestFile(game, files, tempDir,
                    tempOutputFolder.getPath().replace("\\", "/") + "/");
            String generatedTemplateManifestFileUrl = this.elearningFileUploadService.upload(
                    "games/manifest-files/" + game.getId() + "/template.json", generatedTemplateManifestFile);
            // update template manifest url
            game.setGameTemplateManifestUrl(generatedTemplateManifestFileUrl);


            /**
             * DEFAULT MANIFEST
             */
            _log.trace("Generating a manifest file...");
            // auto generate a manifest file
            File generatedManifestFile = GameManifestUtil.generateManifestFile(game, null, tempDir,
                    tempOutputFolder.getPath().replace("\\", "/") + "/");
            // upload generated manifest file to Amazon S3
            String generatedManifestFileUrl = this.elearningFileUploadService
                    .upload("games/manifest-files/" + game.getId() + "/manifest.json", generatedManifestFile);
            // update manifest url
            game.setGameManifestUrl(generatedManifestFileUrl);
            //}

        } catch (IllegalStateException | IOException e) {
            _log.error("Error in processing game files for " + gameZip.getOriginalFilename(), e);

            try {
                FileUtils.deleteDirectory(tempOutputFolder);
            } catch (IOException e1) {
                _log.error("Unable to delete directory", e1);
            }
            return e.getMessage();

        }


        _log.debug("All is well (thank goodness!), update game.");
        gameRepository.save(game); // update game

        // delete directory
        try {
            FileUtils.deleteDirectory(tempOutputFolder);
            FileUtils.forceDelete(zipFile);
        } catch (IOException e) {
            _log.error("Unable to delete file/directory", e);
        }

        return id;
    }

    @Override
    public String uploadThumbnail(String id, MultipartFile thumbnail){
        Game game = gameRepository.findOne(id);

        if (game == null) {
            return "game doesn't exist";// in case the game is void, do nothing
        }

        final String filePath = "games/" + game.getId() + "/thumbnail." + FilenameUtils.getExtension(thumbnail.getOriginalFilename());

        File tempOutputFolder = this.createTempOutputFolder(game.getId());

        File thumbnailFile = new File(tempDir, game.getId() + "/thumbnail." + FilenameUtils.getExtension(thumbnail.getOriginalFilename())); // create a temp file
        this.logTempFileLocation(thumbnailFile);

        String thumbnailUrl;
        try {
            thumbnail.transferTo(thumbnailFile);
            thumbnailUrl = this.elearningFileUploadService.upload(filePath, thumbnailFile );
            game.setThumbnailUrl(thumbnailUrl);
        } catch (IllegalStateException e) {
            _log.error("Unable to upload thumbnail",e);
        } catch (IOException e) {
            _log.error("Unable to upload thumbnail",e);
        }

        gameRepository.save(game);

        try {
            FileUtils.deleteDirectory(tempOutputFolder);
        } catch (IOException e1) {
            _log.error("Unable to delete directory", e1);
        }

        return id;
    }

    private File createTempOutputFolder(String id){
        File tempOutputFolder = new File(tempDir,id);
        if (!tempOutputFolder.mkdir()) {
            _log.warn("Folder already exists. Emtpy-ing the folder.");
            try {
                FileUtils.cleanDirectory(tempOutputFolder);
            } catch (IOException e) {
                _log.error("Unable to clean directory", e);
            }
        }

        return tempOutputFolder;
    }

    private void logTempFileLocation(File file) {
        if(_log.isDebugEnabled()) {
            try {
                _log.debug("Created temporary file: " + file.getName());
                _log.debug("Path: " + file.getPath());
                _log.debug("Absolute Path: " + file.getAbsolutePath());
                _log.debug("Canonical Path: " + file.getCanonicalPath());
            } catch (IOException e) {
                _log.error("Unable to display file path", e);
            }
        }
    }

    private String processIndexFile(Game game, File file, String fullPath) throws IOException {
        String indexPath = "games/" + game.getId() + "/" + fullPath;
        String indexUrl = s3Domain + "/" + indexPath;

        Document indexDocument = Jsoup.parse(file, StandardCharsets.UTF_8.name());

        Elements statsApis = indexDocument.select("script[src$=ikemu_stats_api.js]");
        if(statsApis != null && !statsApis.isEmpty()) {
            Elements changed = statsApis.removeAttr("src").attr("src", apiUrl);
            if(_log.isDebugEnabled()) {
                for (Element el : changed) {
                    _log.debug("Changed src of " + el.nodeName() + " to " + el.attr("src"));
                }
            }
        }

        Elements htmlElement = indexDocument.getElementsByTag("html").removeAttr("manifest");
        _log.debug("Removed manifest from " + htmlElement.size() + " html tag elements");

        try(Writer htmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            _log.debug("Writing the modified index file to " + file);
            htmlWriter.write(indexDocument.html());
            htmlWriter.flush();
        } catch(Exception e) {
            _log.error("Error in writing the modified index file", e);
        }

        return indexUrl;
    }



    private List<File> processFiles(File[] files) {
        List<File> list = new ArrayList<>();
        for (File file : files) {
            if(isValid(file)) {
                if (file.isDirectory()) {
                    list.addAll(processFiles(file.listFiles()));
                } else {
                    _log.trace("Adding file [" + file + "] to list for processing.");
                    list.add(file);
                }
            }
        }
        return list;
    }

    private boolean isValid(File file) {
        final Path p = file.toPath();
        _log.trace("Validating " + p + " [" + p.getFileName() + "]");
        try {
            // general validation
            BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            if(!attr.isRegularFile() && !attr.isDirectory())
                return false;
            if(attr.isSymbolicLink())
                return false;

        } catch (IOException e) {
            _log.warn("Error in validating file [" + p + "]. Marking as not valid: " + e.getLocalizedMessage());
            _log.error(e);
            return false;
        }

        try {
            FileStore store = Files.getFileStore(p);
            if (store.supportsFileAttributeView("dos")) {
                // check for DOS/Windows
                DosFileAttributes dosAttr = Files.readAttributes(p, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
                if(dosAttr.isHidden() || dosAttr.isSystem())
                    return false;
            }
            if (store.supportsFileAttributeView("unix")) {
                // check for Unix/Linux and MacOSX
                final String fileName = file.getName();
                if(fileName.startsWith(".") || fileName.startsWith("__"))
                    return false;
            }
        } catch (IOException e) {
            // swallow exception since we are not sure if the provider is DOS
            _log.warn("Error in checking file attributes for [" + p + "]: " + e.getLocalizedMessage());
        }

        return true;
    }

    private boolean checkFolderName(File[] files){
        List<String> acceptedFolderNames = Arrays.asList("images", "img", "assets", "asset");
        for(File file: files){
            String filename = file.getName().toLowerCase();
            if(file.isDirectory() && acceptedFolderNames.contains(filename)){
                return true;
            }
        }
        return false;
    }


}
