package com.ptit.clone.controller;

import com.ptit.clone.dtos.request.BulkDeleteRequest;
import com.ptit.clone.dtos.request.CreateProductTypeRequest;
import com.ptit.clone.dtos.response.ProductTypeListResponse;
import com.ptit.clone.service.IAdminProductTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/internal/v1/product-types")
@RequiredArgsConstructor
public class AdminProductTypeController {

    private final IAdminProductTypeService adminProductTypeService;

    @PostMapping("/")
    public ResponseEntity<?> createProductType(@Valid @RequestBody CreateProductTypeRequest request) {
        adminProductTypeService.createProductType(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/")
    public ResponseEntity<ProductTypeListResponse> listProductTypes() {
        return ResponseEntity.ok(adminProductTypeService.listProductTypes());
    }

    //Người dùng tick chọn các đối tượng muốn xóa
    @DeleteMapping("/")
    public ResponseEntity<?> deleteProductTypes(@Valid @RequestBody BulkDeleteRequest request) {
        adminProductTypeService.deleteProductTypes(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
