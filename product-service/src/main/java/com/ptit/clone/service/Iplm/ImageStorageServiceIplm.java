package com.ptit.clone.service.Iplm;

import com.ptit.clone.config.properties.ImageStorageProperties;
import com.ptit.clone.service.IImageStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageServiceIplm implements IImageStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp", "gif"
    );

    private final ImageStorageProperties properties;

    private Path rootDir;

    @PostConstruct
    public void init() {
        this.rootDir = Paths.get(properties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootDir);
            log.info("Image upload directory: {}", rootDir);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create image upload directory: " + rootDir, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (file.getSize() > properties.getMaxFileSizeBytes()) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                    "File exceeds max size " + properties.getMaxFileSizeBytes() + " bytes");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Unsupported content type: " + contentType);
        }

        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String ext = StringUtils.getFilenameExtension(original);
        if (ext == null || !ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Unsupported file extension: " + ext);
        }

        String storedName = UUID.randomUUID() + "." + ext.toLowerCase();
        Path target = rootDir.resolve(storedName).normalize();
        if (!target.startsWith(rootDir)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to store file {}", storedName, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot store file", e);
        }

        return storedName;
    }

    @Override
    public List<String> storeAll(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        List<String> urls = new ArrayList<>(files.size());
        for (MultipartFile f : files) {
            if (f != null && !f.isEmpty()) {
                urls.add(store(f));
            }
        }
        return urls;
    }

    @Override
    public Resource loadAsResource(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name is required");
        }
        String cleaned = StringUtils.cleanPath(fileName);
        if (cleaned.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        }
        Path file = rootDir.resolve(cleaned).normalize();
        if (!file.startsWith(rootDir)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
        }
        if (!Files.exists(file) || !Files.isReadable(file)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + fileName);
        }
        try {
            return new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot read file", e);
        }
    }

    @Override
    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        String cleaned = StringUtils.cleanPath(fileName);
        if (cleaned.isEmpty() || cleaned.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        }
        Path file = rootDir.resolve(cleaned).normalize();
        if (!file.startsWith(rootDir)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot delete file", e);
        }
    }

    @Override
    public void deleteByUrl(String url) {
        if (url == null || url.isBlank()) return;
        String base = properties.getPublicBaseUrl();
        if (!url.startsWith(base + "/")) return;
        String fileName = url.substring(base.length() + 1);
        delete(fileName);
    }
}
