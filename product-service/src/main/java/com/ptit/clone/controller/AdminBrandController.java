package com.ptit.clone.controller;

import com.ptit.clone.dtos.request.BulkDeleteRequest;
import com.ptit.clone.dtos.request.CreateBrandRequest;
import com.ptit.clone.dtos.request.UpdateBrandRequest;
import com.ptit.clone.dtos.response.BrandListResponse;
import com.ptit.clone.dtos.response.BrandResponse;
import com.ptit.clone.model.EntityStatus;
import com.ptit.clone.service.IAdminBrandService;
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
@RequestMapping("/internal/v1/brands")
@RequiredArgsConstructor
public class AdminBrandController {

    private final IAdminBrandService adminBrandService;
    private final IImageStorageService imageStorageService;

    @PostMapping(path = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createBrand(
            @Valid @RequestPart("data") CreateBrandRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile
    ) {
        if (logoFile != null && !logoFile.isEmpty()) {
            request.setLogo(imageStorageService.store(logoFile));
        }
        adminBrandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/")
    public ResponseEntity<BrandListResponse> listBrands(@RequestParam(required = false) EntityStatus status) {
        return ResponseEntity.ok(adminBrandService.listBrands(status));
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<BrandResponse> getBrand(@PathVariable UUID brandId) {
        return ResponseEntity.ok(adminBrandService.getBrand(brandId));
    }

    @PatchMapping(path = "/{brandId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable UUID brandId,
            @Valid @RequestPart("data") UpdateBrandRequest request,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile
    ) {
        if (logoFile != null && !logoFile.isEmpty()) {
            request.setLogo(imageStorageService.store(logoFile));
        }
        adminBrandService.updateBrand(brandId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteBrands(@Valid @RequestBody BulkDeleteRequest request) {
        adminBrandService.deleteBrands(request.getIds());
        return ResponseEntity.noContent().build();
    }
}