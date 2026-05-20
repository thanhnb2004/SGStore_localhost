package com.ptit.clone.controller;

import com.ptit.clone.dtos.request.CreateVariantRequest;
import com.ptit.clone.dtos.request.UpdateVariantRequest;
import com.ptit.clone.dtos.response.VariantListResponse;
import com.ptit.clone.service.IAdminVariantService;
import com.ptit.clone.service.IImageStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/internal/v1/variants")
@RequiredArgsConstructor
public class AdminVariantController {

    private final IAdminVariantService adminVariantService;
    private final IImageStorageService imageStorageService;

    @GetMapping("/{productId}")
    public ResponseEntity<VariantListResponse> getAllVariants(@PathVariable UUID productId) {
        return ResponseEntity.ok(adminVariantService.getAllVariants(productId));
    }

    @PostMapping(path = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createVariant(
            @PathVariable UUID productId,
            @Valid @RequestPart("data") CreateVariantRequest request,
            @RequestPart(value = "heroImage", required = false) MultipartFile heroImageFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles
    ) {
        if (heroImageFile != null && !heroImageFile.isEmpty()) {
            request.setHeroImage(imageStorageService.store(heroImageFile));
        }
        if (imageFiles != null && !imageFiles.isEmpty()) {
            request.setImages(imageStorageService.storeAll(imageFiles));
        }
        adminVariantService.createVariant(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping(path = "/{productId}/{variantId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @Valid @RequestPart("data") UpdateVariantRequest request,
            @RequestPart(value = "heroImage", required = false) MultipartFile heroImageFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles
    ) {
        if (heroImageFile != null && !heroImageFile.isEmpty()) {
            request.setHeroImage(imageStorageService.store(heroImageFile));
        }
        if (imageFiles != null && !imageFiles.isEmpty()) {
            request.setImages(imageStorageService.storeAll(imageFiles));
        }
        adminVariantService.updateVariant(productId, variantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{productId}/{variantId}")
    public ResponseEntity<?> deleteVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId
    ) {
        adminVariantService.deleteVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/{variantId}/pause")
    public ResponseEntity<?> pauseVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId
    ) {
        adminVariantService.pauseVariant(productId, variantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/{variantId}/activate")
    public ResponseEntity<?> activateVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId
    ) {
        adminVariantService.activateVariant(productId, variantId);
        return ResponseEntity.ok().build();
    }
}
