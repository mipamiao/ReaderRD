package com.mipa.service;

import com.mipa.common.configuration.MyConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
@Slf4j
@Service
public class FileService {

    @Autowired
    MyConfiguration config;


    public Boolean createDirIfNotExist(String dirPath){
        File dir = new File(dirPath);
        if(dir.exists()){
            return true;
        } else {
            return dir.mkdirs();
        }
    }

    public Boolean saveSmall(MultipartFile file, Path path){
        try{
            byte[] bytes = file.getBytes();
            Files.write(path, bytes);
            return true;
        } catch (IOException e) {
            log.error("Failed to save file: {}", file.getOriginalFilename(), e);
            return null;
        }
    }

    public Boolean deleteSmall(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        } else {
            log.warn("File not found: {}", filePath);
            return false;
        }
    }

    public String generateUniqueFileName(String originalFilename, String prefix) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return prefix + "_" + System.currentTimeMillis() + fileExtension;
    }

    public String combinePath(String ... paths) {
        var stringBuilder = new StringBuilder();
        for (String path : paths) {
            if (!stringBuilder.isEmpty() && !stringBuilder.toString().endsWith(config.fileSeparator)) {
                stringBuilder.append(config.fileSeparator);
            }
            stringBuilder.append(path.startsWith(config.fileSeparator) ? path.substring(1) : path);
        }
        return stringBuilder.toString();
    }

}
