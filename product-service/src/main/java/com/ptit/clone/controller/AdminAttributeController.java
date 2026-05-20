package com.ptit.clone.controller;

import com.ptit.clone.dtos.request.BulkDeleteRequest;
import com.ptit.clone.dtos.request.CreateAttributeRequest;
import com.ptit.clone.dtos.request.UpdateAttributeRequest;
import com.ptit.clone.dtos.response.AttributeDefinitionResponse;
import com.ptit.clone.model.AttributeScope;
import com.ptit.clone.service.IAdminAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/internal/v1/attributes")
@RequiredArgsConstructor
public class AdminAttributeController {

    private final IAdminAttributeService attributeService;

    @PostMapping("/")
    public ResponseEntity<AttributeDefinitionResponse> createAttribute(@Valid @RequestBody CreateAttributeRequest request) {
        attributeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{attributeId}")
    public ResponseEntity<Void> updateAttribute(
            @PathVariable UUID attributeId,
            @Valid @RequestBody UpdateAttributeRequest request
    ) {
        attributeService.updateAttribute(attributeId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product-types/{productTypeId}")
    public ResponseEntity<?> getByProductType(
            @PathVariable UUID productTypeId,
            @RequestParam AttributeScope scope
    ) {
        return ResponseEntity.ok(attributeService.getByProductType(productTypeId, scope));
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteAttributes(@Valid @RequestBody BulkDeleteRequest request) {
        attributeService.deleteAttributes(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
