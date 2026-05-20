package com.ptit.clone.controller;

import com.ptit.clone.dtos.request.CreateProductRequest;
import com.ptit.clone.dtos.request.PublishProductRequest;
import com.ptit.clone.dtos.request.UpdateProductRequest;
import com.ptit.clone.dtos.response.ProductListResponse;
import com.ptit.clone.service.IAdminProductService;
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
@RequestMapping("/internal/v1/products")
@RequiredArgsConstructor
class AdminProductController {

    private final IAdminProductService adminProductService;
    private final IImageStorageService imageStorageService;

    @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @Valid @RequestPart("data") CreateProductRequest request,
            @RequestPart(value = "heroImage", required = false) MultipartFile heroImageFile,
            @RequestPart(value = "variantHeroImage", required = false) MultipartFile variantHeroImageFile,
            @RequestPart(value = "variantImages", required = false) List<MultipartFile> variantImageFiles
    ) {
        if (heroImageFile != null && !heroImageFile.isEmpty()) {
            request.setHeroImage(imageStorageService.store(heroImageFile));
        }
        if (request.getDefaultVariant() != null) {
            if (variantHeroImageFile != null && !variantHeroImageFile.isEmpty()) {
                request.getDefaultVariant().setHeroImage(imageStorageService.store(variantHeroImageFile));
            }
            if (variantImageFiles != null && !variantImageFiles.isEmpty()) {
                request.getDefaultVariant().setImages(imageStorageService.storeAll(variantImageFiles));
            }
        }
        adminProductService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/")
    public ResponseEntity<ProductListResponse> getAllProducts() {
        return ResponseEntity.ok(adminProductService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(adminProductService.getProduct(productId));
    }

    @PatchMapping(path = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestPart("data") UpdateProductRequest request,
            @RequestPart(value = "heroImage", required = false) MultipartFile heroImageFile
    ) {
        if (heroImageFile != null && !heroImageFile.isEmpty()) {
            request.setHeroImage(imageStorageService.store(heroImageFile));
        }
        adminProductService.updateProduct(productId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{productId}/archive")
    public ResponseEntity<?> archiveProduct(@PathVariable UUID productId) {
        adminProductService.archiveProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId) {
        adminProductService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/publish")
    public ResponseEntity<?> publishProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody PublishProductRequest request
    ) {
        adminProductService.publishProduct(productId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
