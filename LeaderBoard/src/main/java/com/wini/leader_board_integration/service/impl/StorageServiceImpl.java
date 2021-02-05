package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.exception.StorageException;
import com.wini.leader_board_integration.service.SequenceService;
import com.wini.leader_board_integration.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by kamal on 1/23/2019.
 */
@Service
public class StorageServiceImpl implements StorageService {
    @Value("${uploadPath}")
    private  String uploadPath;
    @Autowired
    private SequenceService sequenceService;

    @Override
    public String store(MultipartFile file) {
         final Path rootLocation = Paths.get(uploadPath);
//        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String filename =sequenceService.getNextPhotoId()+"."+ StringUtils.getFilenameExtension(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security findBy
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
        return filename;
    }

}
