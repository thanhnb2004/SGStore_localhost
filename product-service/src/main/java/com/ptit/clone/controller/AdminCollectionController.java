package com.ptit.clone.controller;

import com.ptit.clone.dtos.request.BulkDeleteRequest;
import com.ptit.clone.dtos.request.CreateCollectionRequest;
import com.ptit.clone.dtos.request.UpdateCollectionRequest;
import com.ptit.clone.service.IAdminCollectionService;
import com.ptit.clone.service.IImageStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/internal/v1/collections")
@RequiredArgsConstructor
public class AdminCollectionController {

    private final IAdminCollectionService adminCollectionService;
    private final IImageStorageService imageStorageService;

    @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createCollection(
            @Valid @RequestPart("data") CreateCollectionRequest request,
            @RequestPart(value = "heroBanner", required = false) MultipartFile heroBannerFile
    ) {
        if (heroBannerFile != null && !heroBannerFile.isEmpty()) {
            request.setHeroBanner(imageStorageService.store(heroBannerFile));
        }
        adminCollectionService.createCollection(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/")
    public ResponseEntity<?> listCollections() {
        return ResponseEntity.ok(adminCollectionService.listCollections());
    }

    @PatchMapping(path = "/{collectionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCollection(
            @PathVariable UUID collectionId,
            @Valid @RequestPart("data") UpdateCollectionRequest request,
            @RequestPart(value = "heroBanner", required = false) MultipartFile heroBannerFile
    ) {
        if (heroBannerFile != null && !heroBannerFile.isEmpty()) {
            request.setHeroBanner(imageStorageService.store(heroBannerFile));
        }
        adminCollectionService.updateCollection(collectionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteCollections(@Valid @RequestBody BulkDeleteRequest request) {
        adminCollectionService.deleteCollections(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
