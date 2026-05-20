package com.ptit.clone.controller;

import com.ptit.clone.service.IImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLConnection;

@RestController
@RequestMapping("/internal/v1/images")
@RequiredArgsConstructor
class AdminImageController {

    private final IImageStorageService imageStorageService;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> serve(@PathVariable String fileName) throws IOException {
        Resource resource = imageStorageService.loadAsResource(fileName);
        String contentType = URLConnection.guessContentTypeFromName(resource.getFilename());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
